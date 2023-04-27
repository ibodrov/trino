package io.trino.plugin.telemetrycollector.receiver;

import com.google.common.util.concurrent.Striped;
import io.airlift.log.Logger;
import io.opentelemetry.proto.trace.v1.Span;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.HOURS;

public class FilesystemTelemetryStore
        implements TelemetryStore
{
    private static final Logger log = Logger.get(FilesystemTelemetryStore.class);

    private final Path baseDir;
    private final Striped<Lock> locks; // TODO makes more sense for a more sophisticated partitioning scheme

    public FilesystemTelemetryStore(Path baseDir)
    {
        this.baseDir = requireNonNull(baseDir, "baseDir is null");
        try {
            Files.createDirectories(baseDir);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.locks = Striped.lock(32);
    }

    @Override
    public void saveSpans(Collection<Span> spans)
    {
        // TODO batch writes, avoid opening and closing on each write
        spans.forEach(this::saveSpan);
    }

    @Override
    public List<Span> readSpans()
    {
        return findAllPartitions()
                .flatMap(Partition::readAll)
                .toList();
    }

    private void saveSpan(Span span)
    {
        Partition partition = findPartition(span.getStartTimeUnixNano());
        partition.append(span);
        log.debug("Saved a span to %s", partition);
    }

    private Partition findPartition(long startTimeNano)
    {
        // start time as unix timestamp rounded to hour
        long startTimeHour = startTimeNano - (startTimeNano % HOURS.toNanos(1));
        Lock lock = locks.get(startTimeHour);
        Path dest = baseDir.resolve("%d.jsonl".formatted(startTimeHour));
        return new Partition(lock, dest);
    }

    private Stream<Partition> findAllPartitions()
    {
        try {
            return Files.list(baseDir)
                    .filter(f -> f.getFileName().toString().endsWith(".jsonl"))
                    .map(this::openPartition);
        }
        catch (IOException e) {
            throw new TelemetryStoreException("Error while reading spans", e);
        }
    }

    private Partition openPartition(Path file)
    {
        String fileName = file.getFileName().toString();
        long startTimeHour = Long.parseLong(fileName.substring(0, fileName.length() - 6));
        // TODO error handling
        Lock lock = locks.get(startTimeHour);
        return new Partition(lock, file);
    }

    private record Partition(Lock lock, Path file)
    {
        public void append(Span span)
                throws TelemetryStoreException
        {
            String json = JsonHelper.serializeSpan(span);

            try {
                lock.lock();
                try (BufferedWriter writer = Files.newBufferedWriter(file, CREATE, APPEND)) {
                    writer.write(json);
                    writer.newLine();
                }
            }
            catch (IOException e) {
                throw new TelemetryStoreException("Error while saving a span", e);
            }
            finally {
                lock.unlock();
            }
        }

        public Stream<Span> readAll()
        {
            try {
                lock.lock();
                return Files.lines(file).map(JsonHelper::deserializeSpan);
            }
            catch (IOException e) {
                throw new TelemetryStoreException("Error while reading a span", e);
            }
            finally {
                lock.unlock();
            }
        }

        @Override
        public String toString()
        {
            return "File @ " + file;
        }
    }
}

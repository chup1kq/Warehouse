package db.warehouse.domain.importer;

import db.warehouse.domain.entity.Product;
import db.warehouse.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductImporter implements CommandLineRunner {

    private static final Path DATA_DIRECTORY = Path.of("mock-data");
    private static final String FILE_PATTERN = "MOCK_DATA_\\d+\\.csv";

    private final WarehouseRepository warehouseRepository;

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(args).contains("--import")) {
            importProducts();
        }
    }

    private void importProducts() throws IOException {
        if (!Files.exists(DATA_DIRECTORY)) {
            throw new IllegalStateException(
                    "Directory does not exist: " + DATA_DIRECTORY.toAbsolutePath()
            );
        }

        int totalImported = 0;

        try (Stream<Path> files = Files.list(DATA_DIRECTORY)) {

            List<Path> csvFiles = files
                    .filter(Files::isRegularFile)
                    .filter(this::isMockDataFile)
                    .sorted()
                    .toList();

            if (csvFiles.isEmpty()) {
                throw new IllegalStateException(
                        "No MOCK_DATA_*.csv files found in "
                                + DATA_DIRECTORY.toAbsolutePath()
                );
            }

            for (Path file : csvFiles) {
                int imported = importFile(file);
                totalImported += imported;
            }
        }

        System.out.println(
                "Import completed. Total products imported: " + totalImported
        );
    }

    private int importFile(Path file) {
        List<Product> products = new ArrayList<>();

        try (
                var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
                var parser = CSVFormat.DEFAULT.parse(reader)
        ) {
            for (CSVRecord record : parser) {
                if (record.size() != 4) {
                    throw new IllegalArgumentException(
                            "Invalid record in file "
                                    + file.getFileName()
                                    + " at line "
                                    + record.getRecordNumber()
                                    + ". Expected 4 fields, but got "
                                    + record.size()
                    );
                }

                Product product = Product.builder()
                        .name(record.get(0).trim())
                        .description(record.get(1).trim())
                        .price(new BigDecimal(record.get(2).trim()))
                        .quantity(Integer.valueOf(record.get(3).trim()))
                        .build();

                products.add(product);
            }

            warehouseRepository.saveAll(products);

            System.out.println(
                    "Imported "
                            + products.size()
                            + " products from "
                            + file.getFileName()
            );

            return products.size();

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(
                    "Failed to import file: " + file.getFileName(),
                    e
            );
        }
    }

    private boolean isMockDataFile(Path path) {
        return path.getFileName()
                .toString()
                .matches(FILE_PATTERN);
    }
}
package gov.cms.qpp.acceptance.cpc;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;

import static com.google.common.truth.Truth.assertThat;

class CpcPlusAcceptanceTest {

	private static final Path BASE = Paths.get("src/test/resources/cpc_plus/");
	private static final Path SUCCESS = BASE.resolve("success");
	private static final Path FAILURE = BASE.resolve("failure");

	@Test
	void testCpcPlusFileSuccesses() throws IOException {
		Map<Path, AllErrors> errors = new HashMap<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(SUCCESS)) {
			for (Path entry : stream) {
				Files.move(entry, entry.resolveSibling(entry.getFileName().toString().replace("-success.xml", ".xml")));
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
				} catch (TransformException failure) {
					errors.put(entry, failure.getDetails());
				}
			}
		}

		assertThat(errors).isEmpty();
	}

	@Test
	void testCpcPlusFileFailures() throws IOException {
		List<Path> successesThatShouldBeErrors = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(FAILURE)) {
			for (Path entry : stream) {
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
					successesThatShouldBeErrors.add(entry);
				} catch (TransformException expected) {
					System.out.println();
				}
			}
		}

		assertThat(successesThatShouldBeErrors).isEmpty();
	}

	@Test
	void testCpcPlusFilesAreAllChecked() throws IOException {
		long invalidFiles = Files.list(BASE).filter(file -> {
			String fileName = file.toString();

			return fileName.endsWith(".xml");
		}).count();

		assertThat(invalidFiles).isEqualTo(0);
	}
}

package com.axelor.apps.gst.service.invoice;

import com.axelor.data.csv.CSVImporter;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

public class GstInvoiceImportServiceImpl {


	private File getConfigXmlFile() {

		File configFile = null;
		try {
			configFile = File.createTempFile("input-config", ".xml");
			InputStream bindFileInputStream=this.getClass().getResourceAsStream("/data-init/product-config-csv.xml");
			if (bindFileInputStream != null) {
				FileOutputStream outputStream = new FileOutputStream(configFile);
				IOUtils.copy(bindFileInputStream, outputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configFile;
	}

  private File getDataCsvFile(MetaFile dataFile) {

		File csvFile = null;
		try {
			File tempDir = Files.createTempDir();
			csvFile = new File(tempDir, "invoice_line.csv");
			Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return csvFile;
	}

  public void importInvoiceData(MetaFile dataFile, Integer id) throws IOException {
    File configFile = this.getConfigXmlFile();
    File csvFile = this.getDataCsvFile(dataFile);
    CSVImporter importer =
        new CSVImporter(configFile.getAbsolutePath(),csvFile.getParent().toString());
    final Map<String, Object> context = new HashMap<>();
    context.put("invoice", id);
    importer.setContext(context);
    importer.run();
  }
}

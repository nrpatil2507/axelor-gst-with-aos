package com.axelor.apps.gst.web;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.invoice.GstInvoiceService;
import com.axelor.apps.gst.service.invoice.GstInvoiceServiceImpl;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GstInvoiceController {
  @Inject GstInvoiceService gstInvoiceService;
  @Inject ProductRepository productRepo;

  public void updateGstData(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    List<InvoiceLine> invoiceLineList = new ArrayList<>();
    invoiceLineList = gstInvoiceService.updateGst(invoice);
    invoice = Beans.get(GstInvoiceServiceImpl.class).compute(invoice);
    response.setValue("invoiceLineList", invoiceLineList);
    response.setValues(invoice);
  }

  public void printInvoice(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    String name = invoice.getInvoiceId();
    String fileLink =
        ReportFactory.createReport(IReport.GST_INVOICE_REPORT, name + "-${date}")
            .addParam("InvoiceId", invoice.getId())
            .addParam("Locale", ReportSettings.getPrintingLocale(null))
            .generate()
            .getFileLink();
    response.setView(ActionView.define(name).add("html", fileLink).map());
  }

  @SuppressWarnings("unchecked")
  public void getSeletedProducts(ActionRequest request, ActionResponse response) {
    List<Integer> productIdList = (List<Integer>) request.getContext().get("productIds");
    if (productIdList != null) {
      List<InvoiceLine> invoiceLineList = new ArrayList<>();
      for (Integer ProductId : productIdList) {
        Product product = productRepo.find(ProductId.longValue());
        System.out.println(product);
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setProduct(product);
        invoiceLine = gstInvoiceService.setSelectedProductInvoice(invoiceLine);
        invoiceLineList.add(invoiceLine);
      }
      response.setValue("invoiceLineList", invoiceLineList);
    }
  }
}

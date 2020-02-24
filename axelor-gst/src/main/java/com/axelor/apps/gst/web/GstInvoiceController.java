package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.invoice.GstInvoiceService;
import com.axelor.apps.gst.service.invoice.GstInvoiceServiceImpl;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GstInvoiceController {
  @Inject GstInvoiceService gstInvoiceService;

  public void updateGstData(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    List<InvoiceLine> invoiceLineList = new ArrayList<>();
    invoiceLineList = gstInvoiceService.updateGst(invoice);
    invoice = Beans.get(GstInvoiceServiceImpl.class).compute(invoice);
    response.setValue("invoiceLineList", invoiceLineList);
    response.setValue("netIgst", invoice.getNetIgst());
    response.setValue("netCgst", invoice.getNetCgst());
    response.setValue("netSgst", invoice.getNetSgst());
  }
}

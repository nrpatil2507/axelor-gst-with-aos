package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.invoice.GstInvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GstInvoiceLineController {

  @Inject GstInvoiceLineService gstInvoiceLineService;

  public void calculateGst(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);

    Boolean isIgst =
        !invoice.getAddress().getState().equals(invoice.getCompany().getAddress().getState())
            ? true
            : false;

    invoiceLine = gstInvoiceLineService.calculateInvoiceLineGst(invoiceLine, isIgst);
    response.setValues(invoiceLine);
  }
}

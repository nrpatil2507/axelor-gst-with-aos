package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.invoice.GstInvoiceLineService;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GstInvoiceLineController {

  @Inject GstInvoiceLineService gstInvoiceLineService;

  public void calculateGst(ActionRequest request, ActionResponse response) throws AxelorException {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);
    boolean isIgst;
    if (gstInvoiceLineService.checkIsState(invoice)) {
      isIgst =
          !invoice.getAddress().getState().equals(invoice.getCompany().getAddress().getState())
              ? true
              : false;
      invoiceLine = gstInvoiceLineService.calculateInvoiceLineGst(invoiceLine, isIgst);
      response.setValue("igst", invoiceLine.getIgst());
      response.setValue("cgst", invoiceLine.getCgst());
      response.setValue("sgst", invoiceLine.getSgst());

    } else {
      response.setValue("product", null);
      response.setValue("productName", null);
      response.setFlash("pls enter state in address");
    }
  }
}

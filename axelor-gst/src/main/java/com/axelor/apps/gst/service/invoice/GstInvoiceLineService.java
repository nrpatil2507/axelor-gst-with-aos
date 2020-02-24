package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.InvoiceLine;

public interface GstInvoiceLineService {

  public InvoiceLine calculateInvoiceLineGst(InvoiceLine invoiceLine, boolean isIgst);
}

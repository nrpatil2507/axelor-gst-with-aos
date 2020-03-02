package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public interface GstInvoiceLineService {

  public InvoiceLine calculateInvoiceLineGst(InvoiceLine invoiceLine, boolean isIgst);

  public boolean checkIsState(Invoice invoice);
}

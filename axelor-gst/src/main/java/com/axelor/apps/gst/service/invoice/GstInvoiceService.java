package com.axelor.apps.gst.service.invoice;

import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;

public interface GstInvoiceService {
  public List<InvoiceLine> updateGst(Invoice invoice) throws AxelorException;

  public InvoiceLine setSelectedProductInvoice(InvoiceLine invoiceLine);
}

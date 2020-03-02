package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;
import java.util.List;

public interface GstInvoiceService {
  public List<InvoiceLine> updateGst(Invoice invoice) throws AxelorException;

  public InvoiceLine setSelectedProductInvoice(InvoiceLine invoiceLine);
}

package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import java.util.List;

public interface GstInvoiceService {
  public List<InvoiceLine> updateGst(Invoice invoice);
}

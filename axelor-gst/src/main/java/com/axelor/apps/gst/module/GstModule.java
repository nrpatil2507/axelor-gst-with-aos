package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.invoice.GstInvoiceLineService;
import com.axelor.apps.gst.service.invoice.GstInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.invoice.GstInvoiceService;
import com.axelor.apps.gst.service.invoice.GstInvoiceServiceImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceLineProjectServiceImpl.class).to(GstInvoiceLineServiceImpl.class);
    bind(InvoiceServiceProjectImpl.class).to(GstInvoiceServiceImpl.class);
    bind(GstInvoiceLineService.class).to(GstInvoiceLineServiceImpl.class);
    bind(GstInvoiceService.class).to(GstInvoiceServiceImpl.class);
  }
}

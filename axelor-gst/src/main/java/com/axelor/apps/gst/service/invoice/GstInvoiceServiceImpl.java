package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class GstInvoiceServiceImpl extends InvoiceServiceProjectImpl {
  @Inject
  public GstInvoiceServiceImpl(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService,
      AccountConfigService accountConfigService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService,
        accountConfigService);
  }

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {

    invoice = super.compute(invoice);
    BigDecimal igst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        taxTotal = BigDecimal.ZERO,exTaxTotal = BigDecimal.ZERO,inTaxTotal =BigDecimal.ZERO;

    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    if (invoiceLineList != null && !invoiceLineList.isEmpty()) {
      for (InvoiceLine invoiceLine : invoiceLineList) {
        igst = igst.add(invoiceLine.getIgst());
        cgst = cgst.add(invoiceLine.getCgst());
        exTaxTotal = exTaxTotal.add(invoiceLine.getExTaxTotal());
        inTaxTotal = inTaxTotal.add(invoiceLine.getInTaxTotal());
      }
    }
    taxTotal = taxTotal.add(igst).add(cgst).add(cgst);
    invoice.setNetIgst(igst.setScale(2));
    invoice.setNetSgst(cgst.setScale(2));
    invoice.setNetCgst(cgst.setScale(2));

    invoice.setExTaxTotal(exTaxTotal.setScale(2));
    invoice.setInTaxTotal(inTaxTotal.setScale(2));
    invoice.setTaxTotal(taxTotal.setScale(2));

    invoice.setCompanyExTaxTotal(exTaxTotal.setScale(2));
    invoice.setCompanyInTaxTotal(inTaxTotal.setScale(2));
    invoice.setCompanyTaxTotal(taxTotal.setScale(2));

    invoice.setAmountRemaining(inTaxTotal.setScale(2));
    return invoice;
  }
}

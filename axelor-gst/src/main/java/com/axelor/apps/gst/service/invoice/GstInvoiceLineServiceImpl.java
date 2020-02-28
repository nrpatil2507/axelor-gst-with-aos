package com.axelor.apps.gst.service.invoice;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

public class GstInvoiceLineServiceImpl extends InvoiceLineProjectServiceImpl
    implements GstInvoiceLineService {
  @Inject
  public GstInvoiceLineServiceImpl(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService);
  }

  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {
    Map<String, Object> productInformation = super.fillProductInformation(invoice, invoiceLine);
    productInformation.put("gstRate", invoiceLine.getProduct().getGstRate());
    productInformation.put("taxLine", null);
    return productInformation;
  }

  @Override
  public InvoiceLine calculateInvoiceLineGst(InvoiceLine invoiceLine, boolean isIgst) {
    BigDecimal gstAmount = BigDecimal.ZERO;
    BigDecimal invoiceCgst;
    gstAmount =
        invoiceLine.getExTaxTotal().multiply(invoiceLine.getGstRate()).divide(new BigDecimal(100));
    if (isIgst) {
      invoiceLine.setIgst(gstAmount);
    } else {
      invoiceCgst = gstAmount.divide(new BigDecimal(2));
      invoiceLine.setCgst(invoiceCgst);
      invoiceLine.setSgst(invoiceCgst);
    }
    return invoiceLine;
  }

  @Override
  public boolean checkIsState(Invoice invoice) {

    if (invoice.getAddress() != null
        && invoice.getAddress().getState() != null
        && invoice.getCompany().getAddress() != null
        && invoice.getCompany().getAddress().getState() != null) {
      return true;
    } else {
      return false;
    }
  }

 
}

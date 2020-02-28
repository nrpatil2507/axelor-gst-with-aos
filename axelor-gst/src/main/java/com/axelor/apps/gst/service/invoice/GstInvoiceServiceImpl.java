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
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GstInvoiceServiceImpl extends InvoiceServiceProjectImpl implements GstInvoiceService {
	@Inject
	GstInvoiceLineServiceImpl gstInvoiceLineService;

	@Inject
	public GstInvoiceServiceImpl(ValidateFactory validateFactory, VentilateFactory ventilateFactory,
			CancelFactory cancelFactory, AlarmEngineService<Invoice> alarmEngineService, InvoiceRepository invoiceRepo,
			AppAccountService appAccountService, PartnerService partnerService, InvoiceLineService invoiceLineService,
			AccountConfigService accountConfigService) {
		super(validateFactory, ventilateFactory, cancelFactory, alarmEngineService, invoiceRepo, appAccountService,
				partnerService, invoiceLineService, accountConfigService);
	}

	@Override
	public Invoice compute(Invoice invoice) throws AxelorException {
		BigDecimal igst = BigDecimal.ZERO, cgst = BigDecimal.ZERO, taxTotal = BigDecimal.ZERO;
		invoice = super.compute(invoice);
		List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
		if (invoiceLineList != null && !invoiceLineList.isEmpty()) {

			invoice.setNetIgst(BigDecimal.ZERO);
			invoice.setNetCgst(BigDecimal.ZERO);
			invoice.setNetSgst(BigDecimal.ZERO);

			for (InvoiceLine invoiceLine : invoiceLineList) {
				igst = igst.add(invoiceLine.getIgst());
				cgst = cgst.add(invoiceLine.getCgst());
			}
		}
		taxTotal = taxTotal.add(igst).add(cgst).add(cgst);
		invoice.setNetIgst(igst.setScale(2));
		invoice.setNetSgst(cgst.setScale(2));
		invoice.setNetCgst(cgst.setScale(2));
		invoice.setTaxTotal(taxTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
		invoice.setInTaxTotal(invoice.getExTaxTotal().add(taxTotal).setScale(2, BigDecimal.ROUND_HALF_UP));
		invoice.setCompanyInTaxTotal(
				invoice.getCompanyExTaxTotal().add(taxTotal).setScale(2, BigDecimal.ROUND_HALF_UP));
		invoice.setCompanyInTaxTotalRemaining(invoice.getCompanyExTaxTotal().add(taxTotal).setScale(2, BigDecimal.ROUND_HALF_UP));
		return invoice;
	}

	@Override
	public List<InvoiceLine> updateGst(Invoice invoice) throws AxelorException {
		List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
		List<InvoiceLine> invoiceline = new ArrayList<InvoiceLine>();
		Partner partner = invoice.getPartner();
		BigDecimal gstAmount = BigDecimal.ZERO;
		BigDecimal invoiceIgst = BigDecimal.ZERO;
		BigDecimal invoiceCgst = BigDecimal.ZERO;

		if (invoiceLineList != null) {
			if (partner != null) {
				for (InvoiceLine invoiceLine : invoiceLineList) {
					gstAmount = invoiceLine.getExTaxTotal().multiply(invoiceLine.getGstRate())
							.divide(new BigDecimal(100));
					if (Beans.get(GstInvoiceLineServiceImpl.class).checkIsState(invoice)) {
						if (invoice.getCompany().getAddress().getState() == invoice.getAddress().getState()) {
							invoiceCgst = gstAmount.divide(new BigDecimal(2));
						} else {
							invoiceIgst = gstAmount;
						}
					}
					invoiceLine.setCgst(invoiceCgst);
					invoiceLine.setSgst(invoiceCgst);
					invoiceLine.setIgst(invoiceIgst);
					invoiceline.add(invoiceLine);
				}
				
				invoice.setInvoiceLineList(invoiceline);
				invoice=this.compute(invoice);
			}
		}
		return invoiceline;
	}

	@Override
	public InvoiceLine setSelectedProductInvoice(InvoiceLine invoiceLine) {
		Product product = invoiceLine.getProduct();
		if (product != null) {
			invoiceLine.setGstRate(product.getGstRate());
			invoiceLine.setQty(BigDecimal.valueOf(1));
			invoiceLine.setPrice(product.getSalePrice());
			invoiceLine.setName(product.getFullName());
			invoiceLine.setProductName(product.getName());
			invoiceLine.setUnit(product.getUnit());
			invoiceLine.setExTaxTotal(invoiceLine.getPrice().multiply(invoiceLine.getQty()));
		}
		return invoiceLine;
	}
}

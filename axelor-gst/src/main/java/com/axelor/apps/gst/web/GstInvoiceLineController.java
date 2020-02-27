package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.invoice.GstInvoiceLineService;
import com.axelor.common.StringUtils;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GstInvoiceLineController {

	@Inject
	GstInvoiceLineService gstInvoiceLineService;

	public void calculateGst(ActionRequest request, ActionResponse response) {
		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = invoiceLine.getInvoice();
		Boolean isIgst = null;

		if (invoice.getAddress().getState().getName()!=null) {
			isIgst = !invoice.getAddress().getState().equals(invoice.getCompany().getAddress().getState()) ? true
					: false;
			invoiceLine = gstInvoiceLineService.calculateInvoiceLineGst(invoiceLine, isIgst);

		} else {
			response.setError("pls enter state in address");
		}
		response.setValues(invoiceLine);
	}
}

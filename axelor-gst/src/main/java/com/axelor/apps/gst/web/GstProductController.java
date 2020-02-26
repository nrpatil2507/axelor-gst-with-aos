package com.axelor.apps.gst.web;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.gst.report.IReport;
import com.axelor.exception.AxelorException;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.base.Joiner;
import java.util.List;

public class GstProductController {

  @SuppressWarnings("unchecked")
  public void printProducts(ActionRequest request, ActionResponse response) throws AxelorException {
    String productIds = "";
    List<Integer> lstSelectedProduct = (List<Integer>) request.getContext().get("_ids");

    if (lstSelectedProduct != null) {
      productIds = Joiner.on(",").join(lstSelectedProduct);
    }
    String fileLink =
        ReportFactory.createReport(IReport.GST_PRODUCT_REPORT, "Products Detail" + "-${date}")
            .addParam("Products", productIds)
            .generate()
            .getFileLink();
    response.setView(ActionView.define("Product-details").add("html", fileLink).map());
  }

  public void generateNewInvoice(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    List<Integer> idList = (List<Integer>) request.getContext().get("_ids");

    if (idList == null) {
      response.setError("Select Any Product for Invoice Generation");
    } else {
      response.setView(
          ActionView.define("invoices")
              .model(Invoice.class.getName())
              .add("form", "invoice-form")
              .context("productIds", idList)
              .context("_operationTypeSelect", 3)
              .param("show-toolbar", "false")
              .map());
    }
  }
}

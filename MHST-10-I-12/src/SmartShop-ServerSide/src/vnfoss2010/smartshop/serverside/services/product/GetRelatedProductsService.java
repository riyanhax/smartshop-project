package vnfoss2010.smartshop.serverside.services.product;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import vnfoss2010.smartshop.serverside.Global;
import vnfoss2010.smartshop.serverside.database.AccountServiceImpl;
import vnfoss2010.smartshop.serverside.database.ProductServiceImpl;
import vnfoss2010.smartshop.serverside.database.ServiceResult;
import vnfoss2010.smartshop.serverside.database.entity.Product;
import vnfoss2010.smartshop.serverside.services.BaseRestfulService;
import vnfoss2010.smartshop.serverside.services.exception.RestfulException;

import com.google.appengine.repackaged.org.json.JSONObject;
import com.google.gson.JsonObject;

public class GetRelatedProductsService extends BaseRestfulService {
	ProductServiceImpl dbProduct = ProductServiceImpl.getInstance();

	private final static Logger log = Logger.getLogger(AccountServiceImpl.class
			.getName());

	public GetRelatedProductsService(String serviceName) {
		super(serviceName);
	}

	@Override
	public String process(Map<String, String[]> params, String content)
			throws Exception, RestfulException {
		JSONObject json = null;
		try {
			json = new JSONObject(content);
		} catch (Exception e) {
		}

		Long id = new Long(getParameterWithThrow("id", params, json));
		int limit = 0;
		try {
			limit = Integer.parseInt(getParameter("limit", params, json));
		} catch (Exception e) {
		}

		ServiceResult<List<Product>> result = dbProduct.getRelatedProducts(id,
				limit);
		JsonObject jsonReturn = new JsonObject();

		jsonReturn.addProperty("errCode", result.isOK() ? 0 : 1);
		jsonReturn.addProperty("message", result.getMessage());

		if (result.isOK()) {
			jsonReturn.add("products", Global.gsonWithDate.toJsonTree(result
					.getResult()));
		}

		Global.log(log, jsonReturn.toString());
		return jsonReturn.toString();

	}
}

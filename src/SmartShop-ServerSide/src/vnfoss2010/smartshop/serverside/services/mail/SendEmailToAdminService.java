package vnfoss2010.smartshop.serverside.services.mail;

import java.util.Map;

import vnfoss2010.smartshop.serverside.database.ServiceResult;
import vnfoss2010.smartshop.serverside.mail.MailUtils;
import vnfoss2010.smartshop.serverside.services.BaseRestfulService;
import vnfoss2010.smartshop.serverside.services.exception.RestfulException;

import com.google.appengine.repackaged.org.json.JSONObject;

public class SendEmailToAdminService extends BaseRestfulService{

	public SendEmailToAdminService(String serviceName) {
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
		
		String sender = getParameter("sender", params, json);
		String title = getParameter("title", params, json);
		String cont = getParameter("content", params, json);
		
		ServiceResult<Void> result = MailUtils.sendEmailToAdmin(sender, title, cont);
		
		JSONObject jsonReturn = new JSONObject();
		if (result.isOK()) {
			jsonReturn.put("errCode", 0);
		} else {
			jsonReturn.put("errCode", 1);
		}
		jsonReturn.put("message", result.getMessage());
		return jsonReturn.toString();
	}

}

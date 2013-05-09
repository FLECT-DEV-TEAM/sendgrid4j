package jp.co.flect.sendgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

import jp.co.flect.sendgrid.transport.Transport;
import jp.co.flect.sendgrid.transport.TransportUtils;
import jp.co.flect.sendgrid.transport.HttpClientTransport;
import jp.co.flect.sendgrid.model.AbstractRequest;
import jp.co.flect.sendgrid.model.CommonRequest;
import jp.co.flect.sendgrid.model.App;
import jp.co.flect.sendgrid.model.Block;
import jp.co.flect.sendgrid.model.Bounce;
import jp.co.flect.sendgrid.model.WebMail;
import jp.co.flect.sendgrid.model.InvalidEmail;
import jp.co.flect.sendgrid.model.Profile;
import jp.co.flect.sendgrid.model.SpamReport;
import jp.co.flect.sendgrid.model.Statistic;
import jp.co.flect.sendgrid.model.Unsubscribe;
import jp.co.flect.sendgrid.event.Event;
import jp.co.flect.sendgrid.json.JsonUtils;

public class SendGridClient {
	
	private String username;
	private String apikey;
	private Transport transport;
	
	private String baseUrl = "https://sendgrid.com/api";
	
	public SendGridClient(String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
		this.transport = createDefaultTransport();
	}
	
	protected Transport createDefaultTransport() {
		return new HttpClientTransport();
	}
	
	public String getBaseUrl() { return this.baseUrl;}
	public void setBaseUrl(String url) { this.baseUrl = url;}
	
	public Transport getTransport() { return this.transport;}
	public void setTransport(Transport t) { this.transport = t;}
	
	private String doRequest(String path, AbstractRequest request) throws IOException, SendGridException {
		Map<String, String[]> map = request.getParams();
		map.put("api_user", new String[] { this.username});
		map.put("api_key", new String[] { this.apikey});
		
		return this.transport.send(this.baseUrl + path, map);
	}
	
	private void checkResponse(String json) throws SendGridException {
		Map<String, Object> map = JsonUtils.parse(json);
		if (map.get("error") != null || map.get("errors") != null) {
			throw new SendGridException(map);
		}
		if (!"success".equals(map.get("message"))) {
			throw new SendGridException(json);
		}
	}
	
	//Blocks
	public List<Block> getBlocks(Block.Get request) {
		return null;
	}
	
	public void deleteBlocks(Block.Delete request) {
	}
	
	//Bounce
	public List<Bounce> getBounces(Bounce.Get request) {
		return null;
	}
	
	public void deleteBounces(Bounce.Delete request) {
	}
	
	public int countBounces(Bounce.Count request) {
		return 0;
	}
	
	//Filter commands
	public List<App> getAvailableApps() {
		return null;
	}
	
	public void activateApp(String name) {
	}
	
	public void deactivateApp(String name) {
	}
	
	public void setupApp(App app) {
	}
	
	public App getAppSettings(String name) {
		return null;
	}
	
	//Individual apps
	public List<String> getAddressWhilteList() {
		return null;
	}
	
	public void setAddressWhiteList(List<String> list) {
	}
	
	public String getBcc() {
		return null;
	}
	
	public void setBcc(String bcc) {
	}
	
	public boolean isEnableClickTrackingInPlainText() {
		return false;
	}
	
	public void setEnableClickTrackingInPlainText(boolean b) {
	}
	
	public void setDomainKeys(String domain, boolean sender) {
	}
	
	public void setDKIM(String domain, boolean useFrom) {
	}
	
	public void setEventNotification(String url, List<Event> enableEvents) {
	}
	
	//InvalidEmails
	public List<InvalidEmail> getInvalidEmails(InvalidEmail.Get request) throws IOException, SendGridException {
		String json = doRequest("/invalidemails.get.json", request);
		List<Map<String, Object>> list = JsonUtils.parseArray(json);
		List<InvalidEmail> ret = new ArrayList<InvalidEmail>();
		for (Map<String, Object> map : list) {
			ret.add(new InvalidEmail(map));
		}
		return ret;
	}
	
	public void deleteInvalidEmails(InvalidEmail.Delete request) {
	}
	
	//Mail
	public void mail(WebMail mail, File... attachements) throws IOException, SendGridException {
		Map<String, String[]> params = mail.getParams();
		params.put("api_user", new String[] { this.username});
		params.put("api_key", new String[] { this.apikey});
		if (mail.getContent() != null) {
			for (Map.Entry<String, String> entry : mail.getContent().entrySet()) {
				String key = "content[" + TransportUtils.encodeText(entry.getKey()) + "]";
				String value = entry.getValue();
				params.put(key, new String[] { value });
			}
		}
		String xsmtp = mail.getXSmtpApiAsString();
		if (xsmtp != null) {
			params.put("x-smtpapi", new String[] { xsmtp});
		}
		checkResponse(this.transport.send(this.baseUrl + "/mail.send.json", params, attachements));
	}
	
	//Multiple Credentials   - NOT IMPLEMENTED
	//Parse WebHook Settings - NOT IMPLEMENTED
	
	//Profile
	public Profile getProfile() {
		return null;
	}
	
	public void setProfile(Profile profile) {
	}
	
	public void setPassword(String newPassword) {
	}
	
	public void setUsername(String newUsername) {
	}
	
	public void setEmail(String newEmail) {
	}
	
	//SpamReports
	public List<SpamReport> getSpamReports(SpamReport.Get request) {
		return null;
	}
	
	public void deleteSpamReports(SpamReport.Delete request) {
	}
	
	//Statistics
	public List<Statistic> getStatistics(Statistic.Get request) throws IOException, SendGridException {
		String json = doRequest("/stats.get.json", request);
		List<Map<String, Object>> list = JsonUtils.parseArray(json);
		List<Statistic> ret = new ArrayList<Statistic>();
		for (Map<String, Object> map : list) {
			ret.add(new Statistic(map));
		}
		return ret;
	}
	
	public List<String> getCategoryList() throws IOException, SendGridException {
		CommonRequest request = new CommonRequest();
		request.set("list", "true");
		String json = doRequest("/stats.get.json", request);
		List<Map<String, Object>> list = JsonUtils.parseArray(json);
		List<String> ret = new ArrayList<String>();
		for (Map<String, Object> map : list) {
			ret.add(map.get("category").toString());
		}
		return ret;
	}
	
	//Unsubscribles
	public List<Unsubscribe> getUnsubscribes(Unsubscribe.Get request) {
		return null;
	}
	
	public void deleteUnsubscribes(Unsubscribe.Delete request) {
	}
	
	public void addUnsubscribes(Unsubscribe.Add request) {
	}
}

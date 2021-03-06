package com.mall.admin.action;

import com.mall.admin.service.MerchantService;
import com.mall.model.Merchant;
import com.mall.utils.ResponseTemplate;
import com.mall.utils.Token;
import com.mall.utils.sign;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MerchantAction extends AdminBaseAction {

    @Autowired
    private MerchantService merchantService;

    private Merchant merchant = new Merchant();
    private List<Merchant> merchants;

    public Map<String, Object> jsonResult;

    // 列出所有
    public String list() {
        // 分页设置
        if (hasPageSetting()) {
            int page = getPageSetting();
            int pageSize = getPageSizeSetting();

            merchants = merchantService.findByPage(page, pageSize);
        } else {
            merchants = merchantService.findAll();
        }

        Map<String, Object> map = new HashMap<>();

        map.put("data", merchants);
        jsonResult = ResponseTemplate.success(map);
        return SUCCESS;
    }

    // 列出一个
    public String get() {
        if (this.merchant == null) {
            jsonResult = ResponseTemplate.error(-1, "Param merchant is required!");
            return SUCCESS;
        }

        Map<String, Object> map = new HashMap<>();
        merchant = merchantService.findById(this.merchant.getId());
        map.put("data", merchant);
        jsonResult = ResponseTemplate.success(map);
        return SUCCESS;
    }

    // 添加
    public String add() {
        if (this.merchant == null) {
            jsonResult = ResponseTemplate.error(-1, "Param merchant is required!");
            return SUCCESS;
        }

        int status = merchantService.save(this.merchant);
        System.out.println(status);

        if (status > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("data", merchant);
            jsonResult = ResponseTemplate.success(map);
        } else {
            jsonResult = ResponseTemplate.error(-1, "添加失败");
        }
        return SUCCESS;
    }

    // 更新
    public String update() {
        if (this.merchant == null) {
            jsonResult = ResponseTemplate.error(-1, "Param merchant is required!");
            return SUCCESS;
        }

        merchantService.update(merchant);

        Map<String, Object> map = new HashMap<>();
        map.put("data", merchant);
        jsonResult = ResponseTemplate.success(map);
        return SUCCESS;
    }

    // 删除
    public String delete() {
        if (this.merchant == null) {
            jsonResult = ResponseTemplate.error(-1, "Param merchant is required!");
            return SUCCESS;
        }

        merchantService.delete(this.merchant);

        // Set status code
        HttpServletResponse res = ServletActionContext.getResponse();
        res.setStatus(400);

        Map<String, Object> map = new HashMap<>();
        jsonResult = ResponseTemplate.success(map);
        return SUCCESS;
    }

    /**
     * 注册
     * @return
     */
    public String signUp() {
        String encodePwd = sign.md5(merchant.getAdminPass());
        merchant.setAdminPass(encodePwd);
        int result = merchantService.save(merchant);
        if (result > 0) {
            jsonResult = ResponseTemplate.success( null);
        } else {
            jsonResult = ResponseTemplate.error(101, "注册错误");
        }
        return SUCCESS;
    }

    /**
     * 登陆
     * @return
     */
    public String login() {
        Merchant merchant =  merchantService.isAllowLogin(this.merchant.getMerchantName(), this.merchant.getAdminPass());
        if (merchant != null) {
            String str = Token.createToken(merchant, 3600 * 60);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("username", merchant.getMerchantName());
            data.put("desc", merchant.getMerchantName());
            data.put("token", str);
            jsonResult = ResponseTemplate.success(data);
        } else {
            jsonResult = ResponseTemplate.error(102, "登陆失败");
        }
        return SUCCESS;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}

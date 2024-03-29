package test;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.Scanner;

public class PhoneCode {
    private static String code;

    public static void main(String[] args) {
        String phone = ""; //此处可输入你的手机号码进行测试
        Scanner scan = new Scanner(System.in);
        getPhonemsg(phone);
        System.out.println("请输入短信验证码:");
        String userCode = scan.next();
        boolean isCode = isvscode(userCode);
        if (isCode) {
            System.out.println("验证码正确");
        } else {
            System.out.println("验证码错误");
        }
    }

    /**
     * 阿里云短信服务配置
     *
     * @param mobile
     * @return
     */
    public static String getPhonemsg(String mobile) {

        /**
         * 进行正则关系校验
         */
        System.out.println(mobile);
        if (mobile == null || mobile == "") {
            System.out.println("手机号为空");
            return "";
        }
        /**
         * 短信验证---阿里大于工具
         */

        // 设置超时时间-可自行调整
        System.setProperty(StaticPeram.defaultConnectTimeout, StaticPeram.Timeout);
        System.setProperty(StaticPeram.defaultReadTimeout, StaticPeram.Timeout);
        // 初始化ascClient需要的几个参数
        final String product = StaticPeram.product;// 短信API产品名称(短信产品名固定，无需修改)
        final String domain = StaticPeram.domain;// 短信API产品域名(接口地址固定，无需修改)
        // 替换成你的AK
        final String accessKeyId = StaticPeram.accessKeyId;// 你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = StaticPeram.accessKeySecret;// 你的accessKeySecret，参考本文档步骤2
        // 初始化ascClient,暂时不支持多region
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
                    domain);
        } catch (ClientException e1) {
            e1.printStackTrace();
        }

        //获取验证码
        code = vcode();

        IAcsClient acsClient = new DefaultAcsClient(profile);
        // 组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        // 使用post提交
        request.setMethod(MethodType.POST);
        // 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(mobile);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(StaticPeram.SignName);
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(StaticPeram.TemplateCode);// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        //本人的短信模板为   尊敬的用户，您的注册会员动态密码为：${code}，请勿泄漏于他人！  其中${code}为要生成的验证码

        request.setTemplateParam("{ \"code\":\"" + code + "\"}");
        // 可选-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");
        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        // 请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null
                    && sendSmsResponse.getCode().equals("OK")) {
                // 请求成功
                System.out.println("获取验证码成功！！！");
            } else {
                //如果验证码出错，会输出错误码告诉你具体原因
                System.out.println(sendSmsResponse.getCode());
                System.out.println("获取验证码失败...");
            }
        } catch (ServerException e) {
            e.printStackTrace();
            return "由于系统维护，暂时无法注册！！！";
        } catch (ClientException e) {
            e.printStackTrace();
            return "由于系统维护，暂时无法注册！！！";
        }
        return "true";
    }

    /**
     * 生成6位随机数验证码
     *
     * @return
     */
    public static String vcode() {
        String vcode = "";
        for (int i = 0; i < 6; i++) {
            vcode = vcode + (int) (Math.random() * 9);
        }
        return vcode;
    }

    /**
     * 判断验证码是否正确
     *
     * @return boolean true false
     */
    public static boolean isvscode(String userCode) {
        return code.equals(userCode);
    }
}

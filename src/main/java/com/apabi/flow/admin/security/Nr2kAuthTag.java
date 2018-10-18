package com.apabi.flow.admin.security;

import com.apabi.flow.admin.dao.AuthResDao;
import com.apabi.flow.admin.model.AuthRes;
import com.apabi.flow.admin.model.AuthUser;
import com.apabi.flow.admin.service.AuthUserService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class Nr2kAuthTag implements TemplateDirectiveModel {

    @Autowired
    private AuthUserService userService;
    @Autowired
    private AuthResDao authResDao;

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        // 禁止循环变量
        if (loopVars.length != 0) {
            throw new TemplateModelException("This directive doesn't allow loop variables.");
        }
        if (params.containsKey("url")) {
            Object paramValue = params.get("url");
            String resource = ((SimpleScalar) paramValue).getAsString();
            AuthUser user = userService.getCurrUser();
            if (null == user) {
                return;
            }
            List<AuthRes> authress = authResDao.findByUserId(user.getId());

            for (AuthRes authres: authress) {
                if (Objects.equals(resource, authres.getUrl())) {
                    body.render(env.getOut());
                    break;
                }
            }
        }

        if (params.containsKey("property")) {
            String property = params.get("property").toString();
            String[] arr = property.split("\\.");
            if (!"currUser".equals(arr[0]))
                throw new TemplateModelException("no related property...");
            int length = arr.length;
            AuthUser currUser = userService.getCurrUser();
            Writer writer = env.getOut();

            if (length == 1 ) {
                if (params.containsKey("var")) {
                    String var = params.get("var").toString();
                    //获取编译器
                    DefaultObjectWrapperBuilder builder=new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
                    env.setVariable(var, builder.build().wrap(currUser));
                    if (body == null)
                        throw new TemplateModelException("标签体为空！");
                    body.render(writer);
                }else{
                    writer.write(currUser.toString());
                }
            }
            if (length == 2) {
                String str = arr[1];
                if (str.equals(""))
                    throw new TemplateModelException("请指定属性！");
                String methodStr = "get" + str.substring(0,1).toUpperCase() + str.substring(1);
                Class<AuthUser> clazz = AuthUser.class;
                try {
                    Method method = clazz.getMethod(methodStr);
                    Object result = method.invoke(currUser);
                    writer.write(result.toString());
                } catch (NoSuchMethodException e) {
                    throw new TemplateModelException("没有找到相关信息！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
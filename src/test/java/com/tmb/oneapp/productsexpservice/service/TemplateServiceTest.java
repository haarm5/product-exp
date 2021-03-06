package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.config.TemplateEngineConfig;
import com.tmb.oneapp.productsexpservice.model.SoGoodItemInfo;
import com.tmb.oneapp.productsexpservice.model.SoGoodWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class TemplateServiceTest {

    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        TemplateEngineConfig config = new TemplateEngineConfig();

        this.templateService = new TemplateService(config.emailTemplateEngine());
    }

    @Test
    public void testApplySoGoodTemplate() {
        final Context ctx = new Context(Locale.ENGLISH);
        SoGoodWrapper soGoodWrapper = new SoGoodWrapper();
        List<SoGoodItemInfo> soGoodItems = new ArrayList<SoGoodItemInfo>();
        SoGoodItemInfo infoA = new SoGoodItemInfo();
        infoA.setCreateDate("20/04/2021A");
        infoA.setFirstPayment("FirstPaymentA");
        infoA.setName("NameA");
        infoA.setPrinciple("PrincipleA");
        infoA.setTotalAmt("TotalAmtA");
        infoA.setTotalInterest("TotoalIntetestA");
        infoA.setTranDate("TranDateA");
        soGoodItems.add(infoA);

        SoGoodItemInfo infoB = new SoGoodItemInfo();
        infoB.setCreateDate("20/04/2021B");
        infoB.setFirstPayment("FirstPaymentB");
        infoB.setName("NameB");
        infoB.setPrinciple("PrincipleB");
        infoB.setTotalAmt("TotalAmtB");
        infoB.setTotalInterest("TotoalIntetestB");
        infoB.setTranDate("TranDateB");
        soGoodItems.add(infoB);


        soGoodWrapper.setTenor("6");
        soGoodWrapper.setItems(soGoodItems);

        ctx.setVariable("items", soGoodWrapper.getItems());
        ctx.setVariable("tenor", soGoodWrapper.getTenor());


        String html_th = templateService.getHtmlContent("html/applysogood_th.html", ctx);
        assertNotNull(html_th);
        String html_en = templateService.getHtmlContent("html/applysogood_en.html", ctx);
        assertNotNull(html_en);
    }

    @Test
    void getSoGoodItemTh() {
        Context ctx = new Context(Locale.ENGLISH);
        String soGoodItemTh = templateService.getSoGoodItemTh(ctx);
        assertNotNull(soGoodItemTh);
    }

    @Test
    void getSoGoodItemEn() {
        Context ctx = new Context(Locale.ENGLISH);
        String soGoodItemEn = templateService.getSoGoodItemEn(ctx);
        assertNotNull(soGoodItemEn);
    }
}

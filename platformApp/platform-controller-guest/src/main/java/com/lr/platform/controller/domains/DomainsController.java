package com.lr.platform.controller.domains;

import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.response.Response;
import com.lr.platform.service.DomainsService;
import com.lr.platform.utils.Dozer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "方向模块")
@RestController
@RequestMapping("domains")
public class DomainsController {
    /**
     * 服务对象
     */
    @Resource
    private DomainsService domainsService;

    @ApiOperation(value = "获取方向")
    @GetMapping
    public Response selectDomains(){
        Domains domain=new Domains();
        List<Domains> domains = domainsService.queryAll(domain);
        List<DomainsOpVo> domainsVos = Dozer.convert(domains, DomainsOpVo.class);
        return Response
                .builder()
                .len(domainsVos.size())
                .data(domainsVos)
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }


}

package com.korenko.CBlog.service;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Set;

public class IpAddressMatcher implements RequestMatcher {
    private final Set<String> allowedSubnets;

    public IpAddressMatcher(Set<String> allowedSubnets) {
        this.allowedSubnets = allowedSubnets;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "::1".equals(clientIp)) {
            clientIp = "127.0.0.1";
        }

        for (String subnet : allowedSubnets) {
            boolean inSubnet = isInSubnet(clientIp, subnet);
            if (inSubnet) {
                return true;
            }
        }
        return false;
    }

    private boolean isInSubnet(String ip, String subnet) {
        SubnetUtils subnetUtils = new SubnetUtils(subnet);
        return subnetUtils.getInfo().isInRange(ip);
    }
}

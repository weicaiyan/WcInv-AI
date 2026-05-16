package com.wcinv.domain.service;

import com.wcinv.domain.model.CompanyRiskDecision;
import com.wcinv.domain.model.CompanyRiskLevel;
import com.wcinv.domain.model.CompanyRiskSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompanyRiskEvaluator {

    public CompanyRiskDecision evaluate(CompanyRiskSnapshot snapshot) {
        List<String> tags = new ArrayList<>();

        if (snapshot.isStarSt()) tags.add("*ST退市预警");
        if (snapshot.isSt()) tags.add("ST特别处理");
        if (snapshot.isNst()) tags.add("NST/历史ST风险");
        if (snapshot.isScandalFound()) tags.add("重大丑闻/诚信风险");
        if (snapshot.isCsrcInvestigationFound()) tags.add("证监会调查风险");
        if (snapshot.isUnresolvedRecentIssue()) tags.add("近两年问题未落地");
        if (snapshot.isBusinessDeteriorating()) tags.add("经营趋势恶化");
        if (!snapshot.isUserUnderstandsBusiness()) tags.add("不熟悉业务");

        CompanyRiskLevel level;
        String rejectReason = null;
        boolean manualReview = false;

        if (snapshot.isStarSt() || snapshot.isSt() || snapshot.isNst()) {
            level = CompanyRiskLevel.REJECT;
            rejectReason = "名称带 ST / *ST / NST，新手阶段默认不碰。";
            manualReview = true;
        } else if (snapshot.isCsrcInvestigationFound() && snapshot.isUnresolvedRecentIssue()) {
            level = CompanyRiskLevel.REJECT;
            rejectReason = "近两年证监会调查或重大问题未落地，风险无法定价。";
            manualReview = true;
        } else if (snapshot.isScandalFound() || snapshot.isCsrcInvestigationFound() || snapshot.isBusinessDeteriorating()) {
            level = CompanyRiskLevel.HIGH;
            manualReview = true;
        } else if (!snapshot.isUserUnderstandsBusiness()) {
            level = CompanyRiskLevel.MEDIUM;
            manualReview = true;
        } else {
            level = CompanyRiskLevel.LOW;
        }

        String conclusion = buildConclusion(snapshot, level, rejectReason);
        return new CompanyRiskDecision(
                snapshot.getCode(),
                snapshot.getName(),
                level,
                tags,
                rejectReason,
                manualReview,
                conclusion
        );
    }

    private String buildConclusion(CompanyRiskSnapshot snapshot, CompanyRiskLevel level, String rejectReason) {
        String companyName = snapshot.getName() == null || snapshot.getName().trim().isEmpty() ? "这家公司" : snapshot.getName();
        if (level == CompanyRiskLevel.REJECT) {
            return companyName + "先排除：" + rejectReason;
        }
        if (level == CompanyRiskLevel.HIGH) {
            return companyName + "高风险，先别买，必须人工复核。";
        }
        if (level == CompanyRiskLevel.MEDIUM) {
            return companyName + "先观察：你还没说清楚它怎么赚钱。";
        }
        return companyName + "未发现硬性排除项，但这不是买入结论。";
    }
}

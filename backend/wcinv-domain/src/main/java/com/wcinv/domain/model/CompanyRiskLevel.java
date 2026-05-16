package com.wcinv.domain.model;

public enum CompanyRiskLevel {
    LOW("低风险", "通过排雷，但仍需继续看估值和财务"),
    MEDIUM("中风险", "有历史或理解不足风险，先观察"),
    HIGH("高风险", "风险明显，必须人工复核"),
    REJECT("排除", "新手阶段不碰");

    private final String description;
    private final String action;

    CompanyRiskLevel(String description, String action) {
        this.description = description;
        this.action = action;
    }

    public String getDescription() { return description; }
    public String getAction() { return action; }
}

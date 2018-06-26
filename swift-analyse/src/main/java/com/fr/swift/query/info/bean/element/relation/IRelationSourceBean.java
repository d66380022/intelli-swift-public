package com.fr.swift.query.info.bean.element.relation;

import com.fr.swift.query.info.bean.element.relation.impl.FieldRelationSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.RelationPathSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.RelationSourceBean;
import com.fr.swift.source.RelationSourceType;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018/6/26
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = RelationSourceType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelationPathSourceBean.class, name = "RELATION_PATH"),
        @JsonSubTypes.Type(value = FieldRelationSourceBean.class, name = "FIELD_RELATION"),
        @JsonSubTypes.Type(value = RelationSourceBean.class, name = "RELATION")
})
public interface IRelationSourceBean {
    RelationSourceType getType();
}

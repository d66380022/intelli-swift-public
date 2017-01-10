package com.fr.bi.common.persistent.xml.writer;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLBasicValueWriter extends XMLValueWriter {
    public static String BASIC_TAG = "BI_XML_BASIC_TYPE_TAG";

    public XMLBasicValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }


    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        if (beanWrapper.getBean() != null) {
//            if (beanWrapper.getPropertyType() != null && BITypeUtils.isBasicValue(beanWrapper.getPropertyType())) {
//                writer.attr("class", beanWrapper.getPropertyType().getName());
//            } else {
//                writer.attr("class", beanWrapper.getBean().getClass().getName());
//            }
            writer.attr("class", beanWrapper.getBean().getClass().getName());
            writer.startTAG(BASIC_TAG);
            if (XMLNormalValueWriter.USE_CONTENT_SAVE_VALUE) {
                writer.textNode(filterInvalidChar(beanWrapper.getBean().toString()));
            } else {
                writer.attr("value", filterInvalidChar(beanWrapper.getBean().toString()));
            }
            writer.end();
        }
    }

    private String filterInvalidChar(String content) {
        BILoggerFactory.getLogger(XMLBasicValueWriter.class).warn("content is invalid : " + content);
        return content.replaceAll("[\u0000-\u0008\u000b-\u000c\u000e-\u001f]", "");
    }

}
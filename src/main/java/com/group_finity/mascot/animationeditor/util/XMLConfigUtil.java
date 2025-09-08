package com.group_finity.mascot.animationeditor.util;

import com.group_finity.mascot.animationeditor.model.AnimationAction;
import com.group_finity.mascot.animationeditor.model.AnimationPose;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * XML配置文件工具类
 * XML Configuration File Utility
 * 
 * 保持原始XML文件结构完整性，只更新指定的Action元素
 * 
 * @author DCShimeji Team
 */
public class XMLConfigUtil {
    private static final Logger log = Logger.getLogger(XMLConfigUtil.class.getName());
    
    // 缓存原始DOM文档，用于保持结构完整
    private static Document originalDocument;
    
    /**
     * 从XML文件加载动作配置
     * 同时缓存原始文档结构
     */
    public static List<AnimationAction> loadActionsFromXML(File xmlFile) {
        List<AnimationAction> actions = new ArrayList<>();
        
        if (!xmlFile.exists()) {
            log.log(Level.WARNING, "Actions XML file does not exist: " + xmlFile.getPath());
            return actions;
        }
        
        try {
            // 缓存原始文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            originalDocument = builder.parse(xmlFile);
            
            // 只解析包含Animation和Pose的Action（可以编辑的）
            NodeList actionNodes = originalDocument.getElementsByTagName("Action");
            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);
                
                // 只加载包含Animation子元素的Action（其他的保持原样）
                NodeList animationNodes = actionElement.getElementsByTagName("Animation");
                if (animationNodes.getLength() > 0) {
                    AnimationAction action = parseActionElement(actionElement);
                    if (action != null) {
                        actions.add(action);
                    }
                }
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load actions from XML: " + xmlFile.getPath(), e);
        }
        
        return actions;
    }
    
    /**
     * 解析Action元素
     */
    private static AnimationAction parseActionElement(Element actionElement) {
        try {
            String name = actionElement.getAttribute("Name");
            String type = actionElement.getAttribute("Type");
            
            AnimationAction action = new AnimationAction(name, type);
            action.setClassName(actionElement.getAttribute("Class"));
            action.setBorderType(actionElement.getAttribute("BorderType"));
            
            // 解析所有属性，包括基本属性和扩展属性
            NamedNodeMap attributes = actionElement.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                
                // 存储所有属性，包括基本属性（用于完整保存）
                action.setAttribute(attrName, attrValue);
            }
            
            // 解析Animation和Pose元素（如果存在）
            NodeList animationNodes = actionElement.getElementsByTagName("Animation");
            if (animationNodes.getLength() > 0) {
                Element animationElement = (Element) animationNodes.item(0);
                NodeList poseNodes = animationElement.getElementsByTagName("Pose");
                
                for (int j = 0; j < poseNodes.getLength(); j++) {
                    Element poseElement = (Element) poseNodes.item(j);
                    AnimationPose pose = parsePoseElement(poseElement);
                    if (pose != null) {
                        action.addPose(pose);
                    }
                }
            }
            
            return action;
            
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to parse action element", e);
            return null;
        }
    }
    
    /**
     * 解析Pose元素
     */
    private static AnimationPose parsePoseElement(Element poseElement) {
        try {
            AnimationPose pose = new AnimationPose();
            pose.setImage(poseElement.getAttribute("Image"));
            pose.setImageAnchor(poseElement.getAttribute("ImageAnchor"));
            pose.setVelocity(poseElement.getAttribute("Velocity"));
            pose.setSound(poseElement.getAttribute("Sound"));
            
            String durationStr = poseElement.getAttribute("Duration");
            if (!durationStr.isEmpty()) {
                pose.setDuration(Integer.parseInt(durationStr));
            }
            
            String volumeStr = poseElement.getAttribute("Volume");
            if (!volumeStr.isEmpty()) {
                pose.setVolume(Double.parseDouble(volumeStr));
            }
            
            return pose;
            
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to parse pose element", e);
            return null;
        }
    }
    
    /**
     * 保存动作配置到XML文件
     * 只更新修改过的Action，保持其他结构不变
     */
    public static boolean saveActionsToXML(List<AnimationAction> actions, File xmlFile) {
        try {
            if (originalDocument == null) {
                log.log(Level.SEVERE, "No original document cached. Must load first.");
                return false;
            }
            
            // 更新原始文档中的对应Action元素
            for (AnimationAction action : actions) {
                updateActionInDocument(originalDocument, action);
            }
            
            // 保存整个文档，保持原始格式
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            // 保持原始格式设置
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // 不设置standalone，保持原始声明
            
            DOMSource source = new DOMSource(originalDocument);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
            
            return true;
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to save actions to XML: " + xmlFile.getPath(), e);
            return false;
        }
    }
    
    /**
     * 在原始文档中更新指定的Action元素
     */
    private static void updateActionInDocument(Document doc, AnimationAction action) {
        try {
            NodeList actionNodes = doc.getElementsByTagName("Action");
            
            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);
                String actionName = actionElement.getAttribute("Name");
                
                // 找到对应的Action元素
                if (actionName.equals(action.getName())) {
                    // 只更新包含Animation的Action（可编辑的）
                    NodeList animationNodes = actionElement.getElementsByTagName("Animation");
                    if (animationNodes.getLength() > 0) {
                        updateActionElement(actionElement, action);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to update action in document: " + action.getName(), e);
        }
    }
    
    /**
     * 更新Action元素的属性和Animation内容
     */
    private static void updateActionElement(Element actionElement, AnimationAction action) {
        // 更新Action的基本属性
        Map<String, String> attributes = action.getAttributes();
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null && !value.trim().isEmpty()) {
                    actionElement.setAttribute(key, value);
                }
            }
        }
        
        // 更新Animation元素中的Pose列表
        NodeList animationNodes = actionElement.getElementsByTagName("Animation");
        if (animationNodes.getLength() > 0) {
            Element animationElement = (Element) animationNodes.item(0);
            
            // 清除现有的Pose元素
            NodeList poseNodes = animationElement.getElementsByTagName("Pose");
            while (poseNodes.getLength() > 0) {
                Node poseNode = poseNodes.item(0);
                animationElement.removeChild(poseNode);
            }
            
            // 添加更新后的Pose元素
            for (AnimationPose pose : action.getPoses()) {
                Element poseElement = createPoseElement(actionElement.getOwnerDocument(), pose);
                animationElement.appendChild(poseElement);
            }
        }
    }
    
    /**
     * 创建Pose元素
     */
    private static Element createPoseElement(Document doc, AnimationPose pose) {
        Element poseElement = doc.createElement("Pose");
        
        if (pose.getImage() != null && !pose.getImage().isEmpty()) {
            poseElement.setAttribute("Image", pose.getImage());
        }
        
        if (pose.getImageAnchor() != null && !pose.getImageAnchor().isEmpty()) {
            poseElement.setAttribute("ImageAnchor", pose.getImageAnchor());
        }
        
        if (pose.getVelocity() != null && !pose.getVelocity().isEmpty()) {
            poseElement.setAttribute("Velocity", pose.getVelocity());
        }
        
        poseElement.setAttribute("Duration", String.valueOf(pose.getDuration()));
        
        if (pose.getSound() != null && !pose.getSound().isEmpty()) {
            poseElement.setAttribute("Sound", pose.getSound());
        }
        
        if (pose.getVolume() != 1.0) {
            poseElement.setAttribute("Volume", String.valueOf(pose.getVolume()));
        }
        
        return poseElement;
    }
}

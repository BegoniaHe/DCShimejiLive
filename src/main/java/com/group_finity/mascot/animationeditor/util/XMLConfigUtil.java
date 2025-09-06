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
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * XML配置文件工具类
 * XML Configuration File Utility
 * 
 * @author DCShimeji Team
 */
public class XMLConfigUtil {
    private static final Logger log = Logger.getLogger(XMLConfigUtil.class.getName());
    
    /**
     * 从XML文件加载动作配置
     */
    public static List<AnimationAction> loadActionsFromXML(File xmlFile) {
        List<AnimationAction> actions = new ArrayList<>();
        
        if (!xmlFile.exists()) {
            log.log(Level.WARNING, "Actions XML file does not exist: " + xmlFile.getPath());
            return actions;
        }
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList actionNodes = doc.getElementsByTagName("Action");
            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);
                AnimationAction action = parseActionElement(actionElement);
                if (action != null) {
                    actions.add(action);
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
            
            // 解析其他属性
            NamedNodeMap attributes = actionElement.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                action.setAttribute(attr.getNodeName(), attr.getNodeValue());
            }
            
            // 解析Animation和Pose元素
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
     */
    public static boolean saveActionsToXML(List<AnimationAction> actions, File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            // 创建根元素
            Element root = doc.createElement("Mascot");
            root.setAttribute("xmlns", "http://www.group-finity.com/Mascot");
            doc.appendChild(root);
            
            // 创建ActionList元素
            Element actionList = doc.createElement("ActionList");
            root.appendChild(actionList);
            
            // 添加所有Action元素
            for (AnimationAction action : actions) {
                Element actionElement = createActionElement(doc, action);
                actionList.appendChild(actionElement);
            }
            
            // 写入文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
            
            return true;
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to save actions to XML: " + xmlFile.getPath(), e);
            return false;
        }
    }
    
    /**
     * 创建Action元素
     */
    private static Element createActionElement(Document doc, AnimationAction action) {
        Element actionElement = doc.createElement("Action");
        
        // 设置基本属性
        actionElement.setAttribute("Name", action.getName() != null ? action.getName() : "");
        actionElement.setAttribute("Type", action.getType() != null ? action.getType() : "");
        
        if (action.getClassName() != null && !action.getClassName().isEmpty()) {
            actionElement.setAttribute("Class", action.getClassName());
        }
        
        if (action.getBorderType() != null && !action.getBorderType().isEmpty()) {
            actionElement.setAttribute("BorderType", action.getBorderType());
        }
        
        // 设置其他属性
        for (String key : action.getAttributes().keySet()) {
            String value = action.getAttributes().get(key);
            if (value != null && !value.isEmpty()) {
                actionElement.setAttribute(key, value);
            }
        }
        
        // 创建Animation元素
        if (!action.getPoses().isEmpty()) {
            Element animationElement = doc.createElement("Animation");
            actionElement.appendChild(animationElement);
            
            for (AnimationPose pose : action.getPoses()) {
                Element poseElement = createPoseElement(doc, pose);
                animationElement.appendChild(poseElement);
            }
        }
        
        return actionElement;
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

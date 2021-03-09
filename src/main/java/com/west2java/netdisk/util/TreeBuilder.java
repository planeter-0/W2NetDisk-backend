package com.west2java.netdisk.util;

import com.alibaba.fastjson.JSONArray;
import com.west2java.netdisk.entity.Directory;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilder {
    List<Directory> nodes = new ArrayList<>();
    public TreeBuilder(List<Directory> nodes) {
        this.nodes = nodes;
    }

    public String buildJSONTree() {
        List<Directory> treeNodes = buildTree();
        String json = JSONArray.toJSONString(treeNodes);
        return json;
    }

    // 构建树形结构
    public List<Directory> buildTree() {
        List<Directory> treeNodes = new ArrayList<>();
        List<Directory> rootNodes = getRootNodes();//根节点
        for (Directory rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }

    // 递归子节点
    public void buildChildNodes(Directory node) {
        List<Directory> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (Directory child : children) {
                buildChildNodes(child);//递归寻找子节点直至子节点为空
            }
            node.setChildren(children);
        }
    }

    // 获取父节点下所有的子节点
    public List<Directory> getChildNodes(Directory pnode) {
        List<Directory> childNodes = new ArrayList<>();
        for (Directory n : nodes) {
            if (pnode.getId().equals(n.getParentId())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    // 判断是否为根节点，根节点的parentId不可能为任何一个节点的id
    public boolean rootNode(Directory node) {
        boolean isRootNode = true;
        for (Directory n : nodes) {
            if (node.getParentId().equals(n.getId())) {
                isRootNode = false;
                break;
            }
        }
        return isRootNode;
    }

    // 获取集合中所有的根节点
    private List<Directory> getRootNodes() {
        List<Directory> rootNodes = new ArrayList<>();
        for (Directory n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }

    public static int toList(Directory root) {
        if (root != null) {
            if (root.getChildren() != null) {
                List<Directory> dirs = root.getChildren();
                for (Directory dir : dirs) {
                    System.out.println(dir);
                    toList(dir);
                }
            } else {
                return 0;
            }
        }
        return 0;
    }
}

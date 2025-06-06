package com.demo.datastructure.skiplist;

import lombok.ToString;

import java.util.Random;

/**
 * SkipList
 * @see <a href="https://ik3te1knhq.feishu.cn/wiki/IG9hwLI0NiMfZ2k8ePRcfvGVnbb">跳跃表</a>
 */
@ToString
public class SkipList {

    @ToString
    public static class SkipListNode {
        Integer val;
        SkipListNode[] next;

        public SkipListNode(Integer val, int size) {
            this.val = val;
            this.next = new SkipListNode[size];
        }
    }

    private static final int DEFAULT_MAX_LEVEL = 32;
    private static final double DEFAULT_P_FACTORY = 0.25; // 随机出的层数概率
    int currentLevel = 1; // 表示当前层数，从 1 开始

    SkipListNode head = new SkipListNode(null, DEFAULT_MAX_LEVEL);

    public SkipList() {}

    public boolean find(int target) {
        SkipListNode searchNode = head;
        for (int i = currentLevel - 1; i >= 0; i--) { // 按照从高层到低层的顺序查找
            assert searchNode != null;
            searchNode = findClosest(searchNode, i, target);
            if (searchNode != null && searchNode.next[i] != null && searchNode.next[i].val == target) {
                return true;
            }
        }
        return false;
    }

    public SkipListNode findClosest(SkipListNode searchNode, int level, int target) {
        while (searchNode.next[level] != null && searchNode.next[level].val < target) {
            searchNode = searchNode.next[level];
        }
        return searchNode;
    }

    public void add(int val) {
        int level = randomLevel();
        SkipListNode newNode = new SkipListNode(val, level);

        SkipListNode updateNode = head;
        for (int i = currentLevel - 1; i >= 0; i--) {
            updateNode = findClosest(updateNode, i, val);
            if (i < level) {
                if (updateNode.next[i] == null) {
                    updateNode.next[i] = newNode;
                } else {
                    SkipListNode tmp = updateNode.next[i];
                    updateNode.next[i] = newNode;
                    newNode.next[i] = tmp;
                }
            }
        }

        if (level > currentLevel) { // 如果新节点的层数大于当前最大层数
            for (int i = currentLevel; i < level; i++) {
                head.next[i] = newNode; // 把 head 在这些层的 next 指向新节点
            }
            currentLevel = level;
        }
    }

    private int randomLevel() {
        int level = 1;
        Random random = new Random();
        while (random.nextDouble() < DEFAULT_P_FACTORY && level < DEFAULT_MAX_LEVEL) {
            level++;
        }
        return level;
    }

    public void remove() {}

    public void removeLevel() {}

    public static void main(String[] args) {
        SkipList skipList = new SkipList();
        skipList.add(1);
        skipList.add(2);
        skipList.add(3);
        skipList.add(4);
        skipList.add(5);
        skipList.add(6);

        System.out.println(skipList);
    }

}

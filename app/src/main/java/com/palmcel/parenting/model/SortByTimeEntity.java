package com.palmcel.parenting.model;

/**
 * An interface for entity, e.g. comment, like, that can be sort by a time field when they are in
 * an list.
 */
public interface SortByTimeEntity {
    public long getSortTime();
}

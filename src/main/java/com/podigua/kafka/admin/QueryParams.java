package com.podigua.kafka.admin;

import com.podigua.kafka.admin.enums.OffsetType;
import com.podigua.kafka.admin.enums.SearchType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询参数
 *
 * @author podigua
 * @date 2024/03/25
 */
public class QueryParams {
    /**
     * 分区
     */
    private List<Integer> partitions;
    /**
     * 偏移型
     */
    private OffsetType offsetType;
    /**
     * 搜索类型
     */
    private SearchType searchType;

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 抵消
     */
    private Long offset;
    /**
     * 数量
     */
    private Integer count;


    public QueryParams(OffsetType offsetType, SearchType searchType) {
        this.offsetType = offsetType;
        this.searchType = searchType;
    }

    /**
     * 分区
     *
     * @return int
     */
    public List<Integer> partitions() {
        return partitions;
    }

    /**
     * 分区
     *
     * @param partitions 分区
     * @return {@link QueryParams}
     */
    public QueryParams partitions(List<Integer> partitions) {
        this.partitions = partitions;
        return this;
    }

    /**
     * 时间
     *
     * @param time 时间
     * @return {@link QueryParams}
     */
    public QueryParams time(LocalDateTime time) {
        this.time = time;
        return this;
    }

    /**
     * 偏移型
     *
     * @return {@link OffsetType}
     */
    public OffsetType offsetType() {
        return this.offsetType;
    }

    /**
     * 搜索类型
     *
     * @return {@link SearchType}
     */
    public SearchType searchType() {
        return this.searchType;
    }

    /**
     * 时间
     *
     * @return {@link LocalDateTime}
     */
    public LocalDateTime time() {
        return time;
    }

    /**
     * 计数
     *
     * @param count 计数
     * @return {@link QueryParams}
     */
    public QueryParams count(Integer count) {
        this.count = count;
        return this;
    }

    /**
     * 计数
     *
     * @return {@link QueryParams}
     */
    public Integer count() {
        return this.count;
    }

    /**
     * 偏移量
     *
     * @param offset 偏移量
     * @return {@link QueryParams}
     */
    public QueryParams offset(Long offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 偏移量
     *
     * @return {@link QueryParams}
     */
    public Long offset() {
        return this.offset;
    }
}

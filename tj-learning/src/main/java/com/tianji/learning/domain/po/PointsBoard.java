package com.tianji.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学霸天梯榜
 * </p>
 *
 * @author 虎哥
 * @since 2026-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("points_board")
public class PointsBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 榜单id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 积分值
     */
    @TableField("points")
    private Integer points;

    /**
     * 名次，只记录赛季前100
     */
    @TableField("rank")
    private Integer rank;

    /**
     * 赛季，例如 1,就是第一赛季，2-就是第二赛季
     */
    @TableField("season")
    private Integer season;


}

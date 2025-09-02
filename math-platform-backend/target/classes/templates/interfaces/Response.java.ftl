package ${packageName}.interfaces.vo.${entityKey};

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ${description}响应VO
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Data
public class ${entityClass}Response implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

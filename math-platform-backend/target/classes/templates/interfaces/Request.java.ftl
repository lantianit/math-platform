package ${packageName}.interfaces.dto.${entityKey};

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * ${description}请求DTO
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Data
public class ${entityClass}Request implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时必填）
     */
    private Long id;

    // TODO: 根据实际业务需求添加其他字段
    // 例如：名称、描述、状态等
    
    /**
     * 名称
     */
    @NotNull(message = "名称不能为空")
    private String name;
    
    /**
     * 描述
     */
    private String description;
}

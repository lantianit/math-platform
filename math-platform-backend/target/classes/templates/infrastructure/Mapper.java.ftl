package ${packageName}.infrastructure.mapper;

import ${packageName}.domain.${entityKey}.entity.${entityClass};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ${description}Mapper接口
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Mapper
public interface ${entityClass}Mapper extends BaseMapper<${entityClass}> {

}

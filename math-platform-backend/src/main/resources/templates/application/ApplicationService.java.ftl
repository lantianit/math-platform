package ${packageName}.application.service;

import ${packageName}.domain.${entityKey}.entity.${entityClass};

/**
 * ${description}应用服务接口
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
public interface ${entityClass}ApplicationService {

    /**
     * 创建${entityName}
     */
    Long create${entityClass}(${entityClass} ${entityKey});

    /**
     * 删除${entityName}
     */
    Boolean delete${entityClass}(Long id);

    /**
     * 更新${entityName}
     */
    Boolean update${entityClass}(${entityClass} ${entityKey});

    /**
     * 根据ID获取${entityName}
     */
    ${entityClass} get${entityClass}ById(Long id);
}

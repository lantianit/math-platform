package ${packageName}.domain.${entityKey}.repository;

import ${packageName}.domain.${entityKey}.entity.${entityClass};

/**
 * ${description}仓储接口
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
public interface ${entityClass}Repository {

    /**
     * 保存${entityName}
     */
    ${entityClass} save(${entityClass} ${entityKey});

    /**
     * 根据ID查找${entityName}
     */
    ${entityClass} findById(Long id);

    /**
     * 根据ID更新${entityName}
     */
    Boolean updateById(${entityClass} ${entityKey});

    /**
     * 根据ID删除${entityName}
     */
    Boolean removeById(Long id);
}

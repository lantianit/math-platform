package ${packageName}.domain.${entityKey}.service;

import ${packageName}.domain.${entityKey}.entity.${entityClass};

/**
 * ${description}领域服务接口
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
public interface ${entityClass}DomainService {

    /**
     * 创建${entityName}
     */
    void create${entityClass}(${entityClass} ${entityKey});

    /**
     * 删除${entityName}
     */
    void delete${entityClass}(Long id);

    /**
     * 更新${entityName}
     */
    void update${entityClass}(${entityClass} ${entityKey});

    /**
     * 编辑${entityName}
     */
    void edit${entityClass}(${entityClass} ${entityKey});

    /**
     * 校验${entityName}数据
     */
    void valid${entityClass}(${entityClass} ${entityKey}, boolean add);
}

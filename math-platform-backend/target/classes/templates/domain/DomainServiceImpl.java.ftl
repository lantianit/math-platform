package ${packageName}.domain.${entityKey}.service.impl;

import ${packageName}.domain.${entityKey}.entity.${entityClass};
import ${packageName}.domain.${entityKey}.repository.${entityClass}Repository;
import ${packageName}.domain.${entityKey}.service.${entityClass}DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ${description}领域服务实现类
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Service
@Slf4j
public class ${entityClass}DomainServiceImpl implements ${entityClass}DomainService {

    @Resource
    private ${entityClass}Repository ${entityKey}Repository;

    @Override
    public void create${entityClass}(${entityClass} ${entityKey}) {
        // 校验${entityName}数据
        valid${entityClass}(${entityKey}, true);
        
        // 保存${entityName}
        ${entityClass} saved${entityClass} = ${entityKey}Repository.save(${entityKey});
        if (saved${entityClass} == null || saved${entityClass}.getId() == null) {
            throw new RuntimeException("创建${entityName}失败");
        }
        
        log.info("${entityName}创建成功，ID: {}", saved${entityClass}.getId());
    }

    @Override
    public void delete${entityClass}(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("${entityName}ID不能为空");
        }
        
        // 检查${entityName}是否存在
        ${entityClass} existing${entityClass} = ${entityKey}Repository.findById(id);
        if (existing${entityClass} == null) {
            throw new RuntimeException("${entityName}不存在");
        }
        
        // 删除${entityName}
        Boolean removeResult = ${entityKey}Repository.removeById(id);
        if (!removeResult) {
            throw new RuntimeException("删除${entityName}失败");
        }
        
        log.info("${entityName}删除成功，ID: {}", id);
    }

    @Override
    public void update${entityClass}(${entityClass} ${entityKey}) {
        if (${entityKey} == null || ${entityKey}.getId() == null || ${entityKey}.getId() <= 0) {
            throw new RuntimeException("${entityName}信息不能为空");
        }
        
        // 校验${entityName}数据
        valid${entityClass}(${entityKey}, false);
        
        // 检查${entityName}是否存在
        ${entityClass} existing${entityClass} = ${entityKey}Repository.findById(${entityKey}.getId());
        if (existing${entityClass} == null) {
            throw new RuntimeException("${entityName}不存在");
        }
        
        // 更新${entityName}
        Boolean updateResult = ${entityKey}Repository.updateById(${entityKey});
        if (!updateResult) {
            throw new RuntimeException("更新${entityName}失败");
        }
        
        log.info("${entityName}更新成功，ID: {}", ${entityKey}.getId());
    }

    @Override
    public void edit${entityClass}(${entityClass} ${entityKey}) {
        update${entityClass}(${entityKey});
    }

    @Override
    public void valid${entityClass}(${entityClass} ${entityKey}, boolean add) {
        if (${entityKey} == null) {
            throw new RuntimeException("${entityName}数据不能为空");
        }
        
        // TODO: 根据实际业务需求添加校验逻辑
        // 例如：检查必填字段、数据格式、业务规则等
    }
}

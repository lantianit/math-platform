package ${packageName}.interfaces.controller;

import ${packageName}.application.service.${entityClass}ApplicationService;
import ${packageName}.domain.${entityKey}.entity.${entityClass};
import ${packageName}.interfaces.dto.${entityKey}.${entityClass}Request;
import ${packageName}.interfaces.vo.${entityKey}.${entityClass}Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * ${description}控制器
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@RestController
@RequestMapping("/api/${entityKey}")
@Slf4j
public class ${entityClass}Controller {

    @Resource
    private ${entityClass}ApplicationService ${entityKey}ApplicationService;

    /**
     * 创建${entityName}
     */
    @PostMapping
    public ${entityClass}Response create${entityClass}(@RequestBody ${entityClass}Request request) {
        // TODO: 实现请求参数转换和响应封装
        throw new UnsupportedOperationException("方法未实现");
    }

    /**
     * 删除${entityName}
     */
    @DeleteMapping("/{id}")
    public Boolean delete${entityClass}(@PathVariable Long id) {
        return ${entityKey}ApplicationService.delete${entityClass}(id);
    }

    /**
     * 更新${entityName}
     */
    @PutMapping
    public Boolean update${entityClass}(@RequestBody ${entityClass}Request request) {
        // TODO: 实现请求参数转换
        throw new UnsupportedOperationException("方法未实现");
    }

    /**
     * 根据ID获取${entityName}
     */
    @GetMapping("/{id}")
    public ${entityClass}Response get${entityClass}ById(@PathVariable Long id) {
        // TODO: 实现响应封装
        throw new UnsupportedOperationException("方法未实现");
    }
}

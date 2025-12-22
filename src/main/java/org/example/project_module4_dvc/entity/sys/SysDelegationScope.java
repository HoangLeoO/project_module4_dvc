package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "sys_delegation_scopes", schema = "egov_db")
public class SysDelegationScope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "delegation_id", nullable = false)
    private SysUserDelegation delegation;

    @Size(max = 20)
    @NotNull
    @Column(name = "scope_type", nullable = false, length = 20)
    private String scopeType;

    @Size(max = 100)
    @NotNull
    @Column(name = "scope_value", nullable = false, length = 100)
    private String scopeValue;

}
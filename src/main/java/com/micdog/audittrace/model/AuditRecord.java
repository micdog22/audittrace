package com.micdog.audittrace.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="audit_records", indexes={@Index(name="idx_tenant_time", columnList="tenant,ts")})
public class AuditRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String tenant; @Column(nullable=false) private String actor; @Column(nullable=false) private String action;
 private Instant ts=Instant.now(); @Lob private String dataJson; @Column(length=256) private String prevHash; @Column(length=256) private String hash;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getTenant(){return tenant;} public void setTenant(String t){this.tenant=t;}
 public String getActor(){return actor;} public void setActor(String a){this.actor=a;}
 public String getAction(){return action;} public void setAction(String a){this.action=a;}
 public Instant getTs(){return ts;} public void setTs(Instant t){this.ts=t;}
 public String getDataJson(){return dataJson;} public void setDataJson(String d){this.dataJson=d;}
 public String getPrevHash(){return prevHash;} public void setPrevHash(String p){this.prevHash=p;}
 public String getHash(){return hash;} public void setHash(String h){this.hash=h;}
}

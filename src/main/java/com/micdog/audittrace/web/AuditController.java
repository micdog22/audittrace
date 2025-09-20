package com.micdog.audittrace.web;
import com.fasterxml.jackson.databind.ObjectMapper; import com.micdog.audittrace.model.AuditRecord; import com.micdog.audittrace.repo.AuditRecordRepository; import com.micdog.audittrace.service.Hashing;
import org.springframework.http.MediaType; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/audit")
public class AuditController {
 private final AuditRecordRepository repo; private final ObjectMapper mapper=new ObjectMapper();
 public AuditController(AuditRecordRepository repo){ this.repo=repo; }
 public static record AppendReq(String tenant,String actor,String action,Map<String,Object> data){}
 @PostMapping public ResponseEntity<Map<String,Object>> append(@RequestBody AppendReq req) {
   String prev=null; List<AuditRecord> chain=repo.findByTenantOrderByTsAsc(req.tenant()); if(!chain.isEmpty()) prev=chain.get(chain.size()-1).getHash();
   String canonical=Hashing.canonicalJson(req.data()); String payload=(prev==null?"":prev)+"|"+req.tenant()+"|"+req.actor()+"|"+req.action()+"|"+canonical; String hash=Hashing.sha256Hex(payload);
   AuditRecord r=new AuditRecord(); r.setTenant(req.tenant()); r.setActor(req.actor()); r.setAction(req.action()); r.setDataJson(canonical); r.setPrevHash(prev); r.setHash(hash); r=repo.save(r);
   return ResponseEntity.ok(Map.of("id", r.getId(), "hash", r.getHash(), "prevHash", r.getPrevHash()));
 }
 @GetMapping("/{tenant}") public List<AuditRecord> list(@PathVariable String tenant, @RequestParam(defaultValue="100") int limit){ return repo.findTop100ByTenantOrderByTsDesc(tenant); }
 @GetMapping("/{tenant}/verify") public Map<String,Object> verify(@PathVariable String tenant){
   List<AuditRecord> chain=repo.findByTenantOrderByTsAsc(tenant); String prev=null; int idx=0; for(AuditRecord r:chain){ String payload=(prev==null?"":prev)+"|"+r.getTenant()+"|"+r.getActor()+"|"+r.getAction()+"|"+r.getDataJson(); String expected=Hashing.sha256Hex(payload); if(!expected.equals(r.getHash())) return Map.of("ok", false, "breakAt", idx, "id", r.getId()); prev=r.getHash(); idx++; } return Map.of("ok", true, "count", chain.size());
 }
 @GetMapping(value="/{tenant}/export", produces=MediaType.TEXT_PLAIN_VALUE) public String exportCsv(@PathVariable String tenant){
   List<AuditRecord> chain=repo.findByTenantOrderByTsAsc(tenant); StringBuilder sb=new StringBuilder(); sb.append("id,ts,tenant,actor,action,prevHash,hash,dataJson\n");
   for(AuditRecord r:chain){ sb.append(r.getId()).append(",").append(r.getTs()).append(",").append(esc(r.getTenant())).append(",").append(esc(r.getActor())).append(",").append(esc(r.getAction())).append(",").append(r.getPrevHash()==null?"":r.getPrevHash()).append(",").append(r.getHash()).append(",").append(esc(r.getDataJson())).append("\n"); }
   return sb.toString();
 }
 private static String esc(String s){ if(s==null) return ""; return "\""+s.replace("\"", "\"\"")+"\""; }
}

package com.micdog.audittrace.service;
import com.fasterxml.jackson.databind.*; import java.nio.charset.StandardCharsets; import java.security.MessageDigest; import java.util.*;
public class Hashing {
 private static final ObjectMapper mapper=new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
 public static String canonicalJson(Map<String,Object> map){ try{ if(map==null) return "{}"; return mapper.writeValueAsString(new TreeMap<>(map)); }catch(Exception e){ return "{}"; } }
 public static String sha256Hex(String s){ try{ MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] d=md.digest(s.getBytes(StandardCharsets.UTF_8)); StringBuilder sb=new StringBuilder(d.length*2); for(byte b:d) sb.append(String.format("%02x",b)); return sb.toString(); }catch(Exception e){ return ""; } }
}

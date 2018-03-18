package com.dyq.demo.vo;

import lombok.*;

/**
 * 响应 值对象.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Response {
	private Long code;
	private String msg;
	private Long count;
	private Object data;
}

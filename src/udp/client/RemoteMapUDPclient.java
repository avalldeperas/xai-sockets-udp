/*

* Copyright (c) Joan-Manuel Marques 2013. All rights reserved.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
*
* This file is part of the practical assignment of Distributed Systems course.
*
* This code is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This code is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this code.  If not, see <http://www.gnu.org/licenses/>.
*/

package udp.client;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoc.dpcs.lsim.logger.LoggerManager.Level;
import lsim.library.api.LSimLogger;


/**
 * @author Joan-Manuel Marques
 *
 */

public class RemoteMapUDPclient {

	public RemoteMapUDPclient() {
	}
	
	public Map<String, String> getMap (List<Key> keys) {
		Map<String, String> map = new HashMap<String, String>();
		int i = 1;
		for (Key key : keys) {
			LSimLogger.log(
					Level.TRACE,
					"["+i+"] Query for key "+key.getKey()+" at "+ key.getServerAddress() +":"+key.getServerPort()
					);

			String value = get(key.getKey(), key.getServerAddress(), key.getServerPort());

			LSimLogger.log(Level.TRACE, "["+i+"] RemoteMap("+key.getKey()+"): "+ value);
			i++;
			map.put(key.getKey(), value);
		}

		return map;
	}
	
	private String get(String key, String server_address, int server_port){
		LSimLogger.log(Level.INFO, "inici RemoteMapUDPclient.get ");
		LSimLogger.log(Level.TRACE, "key: " + key);
		LSimLogger.log(Level.TRACE, "server_address: " + server_address);
		LSimLogger.log(Level.TRACE, "server_port: " + server_port);
		
		String resposta = null;
		
		return sendReceive(key, server_address, server_port);
	}

	private String sendReceive(String key, String server_address, int server_port) {
		InetAddress addr;
		DatagramSocket socket;
		DatagramPacket packetSend, packetReceive;
		byte[] bytes_message = new byte[256];
		String response = null;
		
		try {
			socket = new DatagramSocket();			
			addr = InetAddress.getByName(server_address);
			
			packetSend = new DatagramPacket(key.getBytes(), key.length(), addr, server_port);
			LSimLogger.log(Level.INFO, "Enviant petició " + key + " a la adreca " + server_address +":" + server_port);
			socket.send(packetSend);
			LSimLogger.log(Level.INFO, "Enviat!");

			packetReceive = new DatagramPacket(bytes_message, bytes_message.length);
			LSimLogger.log(Level.INFO, "Esperem la resposta del servidor...");

			socket.receive(packetReceive);
			response = new String(packetReceive.getData(), packetReceive.getOffset(), packetReceive.getLength());
			LSimLogger.log(Level.INFO, "Resposta Rebuda: " + response);
			
			socket.close();
		} catch (SocketException | SecurityException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return response;
	}
}
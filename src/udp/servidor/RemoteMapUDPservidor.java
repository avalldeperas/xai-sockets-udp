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

package udp.servidor;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.uoc.dpcs.lsim.logger.LoggerManager.Level;
import lsim.library.api.LSimLogger;

/**
 * @author Joan-Manuel Marques
 *
 */

public class RemoteMapUDPservidor {
	
	public RemoteMapUDPservidor(int server_port, Map<String, String> map){
		LSimLogger.log(Level.INFO, "Inici RemoteMapUDPservidor ");
		LSimLogger.log(Level.INFO, "server_port: " + server_port);
		LSimLogger.log(Level.INFO, "map: " + map);

		DatagramSocket socket;
		try {
			socket = new DatagramSocket(server_port);
			
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					receiveSend(map, socket);
				}
			};
			Timer timer = new Timer("Timer");
			timer.schedule(task, 3000, 3000);
			
		} catch (SocketException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} 
	}

	private void receiveSend(Map<String, String> map, DatagramSocket socket) {
		byte[] bytes_message = new byte[256];
		DatagramPacket packetReceive, packetSend;
				
		try {
			// Receive packet
			packetReceive = new DatagramPacket(bytes_message, bytes_message.length);
			socket.receive(packetReceive);
			LSimLogger.log(Level.INFO, "Adreça IP del remitent: " + packetReceive.getAddress());
			LSimLogger.log(Level.INFO, "Port del remitent: " + packetReceive.getPort());
			String response = buildResponse(map, packetReceive);			
			
			// Send packet
			packetSend = new DatagramPacket(response.getBytes(), response.length(), packetReceive.getAddress(), packetReceive.getPort());
			LSimLogger.log(Level.INFO, "Responent el valor '" + response + "' a " + packetReceive.getAddress() +":" + packetReceive.getPort());
			socket.send(packetSend);
			LSimLogger.log(Level.INFO, "Enviat!");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	private String buildResponse(Map<String, String> map, DatagramPacket packetReceive) {
		String message = new String(packetReceive.getData(), packetReceive.getOffset(), packetReceive.getLength());
		
		return map.getOrDefault(message, "key-not-found");
	}
}

/*******************************************************************************
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is MundoCore Java.
 * 
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.android.rdps.bluetooth.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.psi.ipen.PenSample;

/**
 * The IO2StreamDecoder class converts the byte stream coming from 
 * the Bluetooth Serial Port Profile into RawDataSamples by assuming that 
 * the source is a Logitech IO2 digital pen.
 * This code has been taken out of the letras-java distribution
 * <p>
 * @author niklas
 * @version 0.0.1
 *
 */
public class IO2StreamDecoder extends Decoder{

	//logger
	private static Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.logitech");

	//IO2 protocol specific values

	/**
	 * States of the transmission.
	 */
	private static enum StreamingState {
		HEADER, PAYLOAD
	}

	/**
	 * Identifier for new session 
	 */
	private static final byte ID_PEN_CONNECT = 0x02;

	/**
	 * PenUP Event Identifier
	 */
	private static final byte ID_PEN_UP = 0x04;

	/**
	 * Coord Event Identifier
	 */
	private static final byte ID_SIMPLE_COORD = 0x03;

	/**
	 * coordinates of the origin of the page. This is only 
	 * temporary until the origin can be computed from the page address
	 */

	static int xorigin, yorigin;

	//members

	/**
	 * field to permanently store the reference time for calculation between the pen internal
	 * time and pen external time
	 */
	private long timestampSessionBegin = 0;

	/**
	 * field to temporarily store the page address 
	 */
	private long pageAddressBuffer = 0L;

	/**
	 * field to temporarily store the relative x coordinate in 1/8 of anoto coordinates 
	 */
	private int relx = 0;

	/**
	 * field to temporarily store the relative y coordinate in 1/8 of anoto coordinates 
	 */
	private int rely = 0;

	/**
	 * field to temporarily store the force value (between 0 and 255) 
	 */
	private int force = 0;

	/**
	 * field to temporarily store the pen internal timestamp
	 */
	private long timestampBuffer = 0L;

	/**
	 * pen internal penID
	 * Not used yet
	 */
	private long penID = 0;

	/**
	 * protocol version
	 * Not used yet (for the IO2 version = 2)
	 */
	private int protocolVersion = 0;

	/**
	 * field which identifies the currently transmitted part of the packet
	 */
	private StreamingState currentProtocolState = StreamingState.HEADER;

	/**
	 * field to store the type of packet that is currently read in
	 */
	private int packetType;

	/**
	 * Counter for the received number of bytes for the coordinate in transmission
	 */
	private int numBytesReceived;


	/**
	 * store the length of the packet which is currently received
	 */
	private int lengthBuffer = 0;

	/**
	 * State of the pen. This is needed because the pen doesn't issue an explicit PenDown event.
	 */
	private boolean penIsUp = true;

	/**
	 * Standard constructor
	 */
	public IO2StreamDecoder() {
		currentProtocolState = StreamingState.HEADER;
	}

	public void decode(InputStream inputStream) {

		int currentByte;
		boolean streaming = false;
		
		while(true) {
			try {
				currentByte = inputStream.read();
			} catch (IOException e) {
				break;
			}
			
			if (currentByte == -1) {
				//here we're truncating leading "-1" from the stream
				//however ones we're streaming, we shutdown if "-1" is received
				if (streaming)
					break;
				else
					continue;
			} else {
				streaming = true;
			}
			
			//check in which protocol state we are in (header or payload)
			if (currentProtocolState == StreamingState.HEADER) {
				//header has 3 bytes
				switch (numBytesReceived) {
				case 0:
					//first byte is the packet type
					packetType = currentByte;
					break;
				case 1:
					//second and third bytes are the payload length
					lengthBuffer = currentByte;
					break;
				case 2:
					lengthBuffer = (lengthBuffer << 8) | currentByte;
					//check for pen up event
					if (packetType == ID_PEN_UP) {
						penUp();
						penIsUp = true;
					}  else {
						//next up will be the payload for this header
						currentProtocolState = StreamingState.PAYLOAD;
					}
					numBytesReceived = 0;
					continue;
				default:
					break;
				} 
				numBytesReceived++;
			} else if (currentProtocolState == StreamingState.PAYLOAD) {
				//receiving a byte of the payload
				if (numBytesReceived < lengthBuffer) {
					if (packetType == ID_SIMPLE_COORD) {
						//receiving a coordinate sample
						if (numBytesReceived < 8) {
							timestampBuffer = (timestampBuffer << 8) | currentByte;
						} else if (numBytesReceived < 16) {
							pageAddressBuffer = (pageAddressBuffer << 8) | currentByte;
						} else if (numBytesReceived < 18) {
							relx = (relx << 8) | currentByte;
						} else if (numBytesReceived < 20 ) {
							rely = (rely << 8) | currentByte;
						}	else if (numBytesReceived < 21) {
							force = currentByte;
							if (numBytesReceived == 20) {
								//sample complete; create PenSample instance
								final PenSample ps = new PenSample(calculateX(),calculateY(),calculateForce(), calculateTime());
								if (penIsUp) {
									penDown();
									penIsUp = false;
								}
								receivedSample(ps);
							}
						}
					} else if (packetType == ID_PEN_CONNECT) {
						//receiving the session setup packet
						if (numBytesReceived < 2) {
							protocolVersion = (protocolVersion << 8) | currentByte;
						} else if (numBytesReceived < 10) {
							timestampBuffer = (timestampBuffer << 8) | currentByte;
						} else if (numBytesReceived < 18) {
							penID = (penID << 8) | currentByte;
							if (numBytesReceived == 17) {
								if (protocolVersion == 2) {
									//calculate the difference between real time and pen internal time;
									timestampSessionBegin = System.currentTimeMillis() - timestampBuffer;
									logger.logp(Level.FINE, "IO2StreamDecoder", "handleByte", "received correct header");
								} else 
									logger.logp(Level.FINE, "IO2StreamDecoder", "handleByte", "pen uses wrong protocol version");
							}
						}
					}
					numBytesReceived++;
				} 
				if (numBytesReceived == lengthBuffer) {
					//reset all fields
					packetType = 0;
					pageAddressBuffer = 0;
					force = 0;
					relx = 0;
					rely = 0;
					timestampBuffer = 0;
					lengthBuffer = 0;
					currentProtocolState = StreamingState.HEADER;
					numBytesReceived = 0;
				}
			}
		}
	}

	/**
	 * to get the real time we have to calculate timestampSessionBegin + timeStampBuffer
	 * @return the real time for the received event
	 */
	private long calculateTime() {
		return timestampSessionBegin + timestampBuffer ;
	}

	/**
	 * precalculated values for fraction bits
	 */
	private float[] fractionMap = {0.0f, 0.125f, 0.250f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f};

	/**
	 * calculate the absolute position in anoto coordinate space for the x-coordinate
	 * @return absolute x-coordinate
	 */
	private double calculateX() {
		return (double) xorigin + (relx >> 3) + fractionMap[relx & 0x7];
	}

	/**
	 * calculate the absolute position in anoto coordinate space for the y-coordinate
	 * @return absolute y-coordinate
	 */
	private double calculateY() {
		return (double) yorigin + (rely >> 3) + fractionMap[rely & 0x7] ;
	}

	/**
	 * invert the force value
	 * @return inverted force value
	 */
	private int calculateForce() {
		return 255-force;
	}
}

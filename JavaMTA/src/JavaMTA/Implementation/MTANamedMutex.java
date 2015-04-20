/*
	MTANamedMutex.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package JavaMTA.Implementation;

import java.util.concurrent.Semaphore;

// Class NamedMutex
// ------------------------------------------------------------------
public class MTANamedMutex extends Semaphore
{
	private static final long serialVersionUID = 1L;

	private final String name;

	// MTANamedMutex
	// ------------------------------------------------------------------
	public MTANamedMutex(String name)
	{
		super(1);	// Creates a semaphore with 1 access only

		this.name = name;
	}

	// getName
	// ------------------------------------------------------------------
	public String getName()
	{
		return name;
	}		
}

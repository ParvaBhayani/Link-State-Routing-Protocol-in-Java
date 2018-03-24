import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Link_State_Routing {

	public static int usercommand; 							//scans the input command
	public static String top_file; 							// scan user input file 
	public static int[][] matrix_form; 						//Network topology
	public static int rows,columns; 						//scans rows and columns in Network Table
	public static int srouter; 								//source router for the Network
	public static int del_router; 							//router deleted from the Network
	public static int drouter; 								//destination router for the Network
	public static int[] forward_table;						//Forward table according to source and destination set.
	public static int[] s2d; 								//path from source to destination(router)
	public static int[] enroute; 							//total distance 
	public static int[] state_del_router;				 	//stores the state of the deleted router
	public static int[][] shortestdist; 					//shortest distance from router to others
	
	//Get File and create Network Topology from matrix in the file
	public static void Matrix(String filename) 
	{
		Scanner file;
		try {
			file = new Scanner(new File(filename));
		 
		rows = 0;
		columns = 0;
		String weight;
		String[] counts;
	
		do
		{
			counts = file.nextLine().split("\\s+");
			if(columns != counts.length)
			{
				if(columns!=0)
				{
					top_file=null;
					System.out.println("Please Enter valid Network Topology");
					return;
				}
			}
			columns = counts.length;
			rows++;
		}while(file.hasNextLine());
		
		System.out.println();
		System.out.println("no of rows: "+ rows +" no of columns: "+columns);
		System.out.println();
		
		if(rows != columns)				//if not square matrix than invalid file
		{
			top_file=null;
			System.out.println("Please Enter Proper Network Topology!");
			return;
		}	
		
		matrix_form=new int[rows][columns];
		forward_table=new int[rows];
		state_del_router=new int[rows];
		for(int row=0;row<rows;row++)
		{
			state_del_router[row]=0;
		}
		
		shortestdist=new int[rows][columns];
		s2d=new int[rows];
		Scanner file1;
		file1=new Scanner(new File(filename));
		String[] weight_counts;
		weight=file1.nextLine();
		int rw=0;
			System.out.println("Network Topology is as follow ");
			System.out.println("==============================================");
			while(rw<rows)	
			{
				weight_counts=weight.split("\\s+");
				int col=0;
				while(col<columns)
				{
					matrix_form[rw][col]=Integer.parseInt(weight_counts[col]);
					System.out.print(matrix_form[rw][col]+" ");
					col++;
				}
				if(file1.hasNextLine())
				{
					weight=file1.nextLine();
				}
				System.out.println();
				rw++;
			}
			System.out.println("==============================================");
			System.out.println();
			for(int r=0;r<rows;r++)				//check diagonal not equal to zero.
			{
				if(matrix_form[r][r] !=0)
				{
					top_file=null;
					System.out.println("Enter a valid Topology whose diagonal should be zero");
					System.out.println();
					return;
							
				}
			}		
	}
	catch (Exception e) 
		{
			top_file=null;
			System.out.println("Please Enter a valid text file with Extension");
			System.out.println();
		}	
	}

	/*method to create forward table from a source node in Network*/
	public static void ForwardTable(int[][] input_matrix, int src)
	{
		enroute=new int[rows];
		boolean discovered[];
		discovered=new boolean[rows];
		forward_table[src]=src;
		s2d[src]=-1;
		int r=0;
		while(r<rows)
		{
			for(int c=0;c<columns;c++)
			{
				if(input_matrix[r][c] == -1)
				{
					input_matrix[r][c]=0;
				}
			}
			//System.out.println();
			enroute[r]=Integer.MAX_VALUE;
			discovered[r]=false;
			r++;
		}
		enroute[src]=0;
		int i=0;
		while(i<rows-1)
		{
			  	int discoverednode = mPath(discovered,enroute);
	            discovered[discoverednode] = true;
	            int x=0;
	            
	            while(x<columns)
	            {
	                if (!discovered[x])
	                {
	                	if(input_matrix[discoverednode][x]!=0)
	                    {
	                		if(enroute[discoverednode] != Integer.MAX_VALUE)
	                		{
	                			if(enroute[discoverednode]+input_matrix[discoverednode][x] < enroute[x])
	                			{
	                				enroute[x] = enroute[discoverednode] + input_matrix[discoverednode][x];
			                		forward_table[x]=discoverednode;
			                		s2d[x]=discoverednode;
	                			}
	                		}
	                    }
	                }
	                x++;
	            }
	            i++;
		}
		int p=0;
		while(p<rows)
		{
				shortestdist[src][p]=enroute[p];
				p++;
		}	
	}
	//method for finding the shortest path between the source and the destination 
		public static int mPath(boolean discovered[],int enroute[])
	    {
	        int m = Integer.MAX_VALUE, m_index=-1;
	        int v=0; 
	        while(v<rows)        
	        {
	            if (discovered[v] == false)
	            {
	            	if(enroute[v] <= m)
	            	{
	            		  m = enroute[v];
	                      m_index = v;
	                  }
	            	}
	              
	            v++;
	        }
	        return m_index;
	    }
	//method for printing the forward table for the node in the Network
	public static void Print_Forward_Table(int src)
	{
		System.out.println("Router "+ srouter + " Connection Table");
		System.out.println("Destination");
		System.out.println("Router               Interface");
		System.out.println("==============================================");
		int p=0;
		while(p<rows)
        {
				 if(p==forward_table[src])
		         {
		              System.out.println(" "+(p+1)+"\t\t\t -" );
		         }
				 else if(state_del_router[p]==1)
				 {	
				     //forward_table[i]=Integer.MAX_VALUE;
					 System.out.println(" "+(p+1)+"\t\t\t -" );
				 }
				 else if(state_del_router[forward_table[p]]==1)
				 {
					 System.out.println(" "+(p+1)+"\t\t\t -" );
				 }
	            else
	            {
		                if(src == forward_table[p])
		                {
		                	
		                	System.out.println(" "+(p+1)+"\t\t\t "+(p+1));
		                	
		                }
		                else
		                {
		                	
		                	int inx=forwardconnecting(p);
		                	if(inx==-2)
		                	{
		                		System.out.println(" "+(p+1)+"\t\t\t -");		                
		                	}
		                	else
		                	{
		                		System.out.println(" "+(p+1)+"\t\t\t "+(inx+1));
		                	}
		                }
	            }
				 p++;
        }
	
	}	 
	//method for building the forward table for the node in the network 
	public static int forwardconnecting(int index)
	{
		int neighbour=0;
		
		while(index != srouter-1)
		{
			if(state_del_router[forward_table[index]]==1)
			{
				//return 0;
				return -2;
			}
			neighbour=index;
			
			if(index <=rows)
			{
				index=forward_table[index];
			}
		}	
		return neighbour;
	}
	//method for removing a router
		public static void deleteRouter(int del_router)
		{
			 System.out.println("Review Topology Matrix: ");
	         int i=0;
			 while(i<rows)
	         {
				 int j=0;
	             while(j<columns)
	             {
	            	 
		                  if(matrix_form[i][j]==0)
		                  {
		                	 if(i==j)
		            		 {
		            			 matrix_form[i][j]=0;
		            		 }
		            		 else
		            		 {
		            			 matrix_form[i][j]=-1;
		            		 }
		                  }
		                  else if(i==del_router)
		                  {
		                	 matrix_form[i][j]=-1;
		                  }
		                  else if(j==del_router)
		                  {
		                	  matrix_form[i][j]=-1;
		                  }
	            	             	 
	            	 System.out.print(matrix_form[i][j]+" ");
	            	 j++;
	             }
	             System.out.println("");
	             i++;
	         }	
			 state_del_router[del_router]=1;//setting state of deleted router
	         if((del_router+1)==srouter)
	         {
	        	 srouter=0;
	        	 System.out.println("Source Router is Deleted!!");
	        	System.out.println("Please Select Source Router using Input Command 2");
	        	System.out.println();
	         }
	         else if((del_router+1)==drouter)
	         {
	        	 drouter=0;
	        	 System.out.println("Destination Router is Deleted!!");
	        	 System.out.println("Please Select Destination Router Through Input Command 3");
	        	 System.out.println();
	         }
	         else
	         {
	        	System.out.println("After Deleting Router "+(del_router+1));
	        	System.out.println("New Forward table and Path from source to destination is as follows:");
	        	ForwardTable(matrix_form,srouter-1);
	            Print_Forward_Table(srouter-1);
	            ShortestPath(drouter-1);        	
	         }	
		}
		
		//method for displaying the shortest path between two nodes 
		 
		public static void ShortestPath(int finalnode) 
		{
			if(enroute[finalnode]==Integer.MAX_VALUE)
	        {
	            System.out.println("Path does not exist between Source Router "+srouter+" and Destination Router "+drouter);
	        }
			else
			{
				System.out.print("The Shortest Path from Router "+srouter+" to "+drouter+" is "+srouter);
				printPath(s2d,finalnode);
				System.out.println();
				System.out.println("The Total Cost is "+enroute[finalnode]);
			}
			
		}
		//method for finding the best router 
		public static void BestBrodcastRouter(int[][] inputMatrix) 
		{
			int[] optimalNode = new int[rows];
			int a=0;
			while(a<rows)
			{
				ForwardTable(inputMatrix,a);
				a++;
			}
			
	        //calculating total path 
			int r=0;
	        while(r<rows)
	        {
	            int total=0;
	            int c=0;
	            while(c<columns)
	            {
	            	if(shortestdist[r][c] == Integer.MAX_VALUE)
	            	{
	            		shortestdist[r][c]=0;
	            	}
	                total+=shortestdist[r][c];
	                c++;
	            }
	            optimalNode[r]= total;
	        r++;
	        }
	        int m = Integer.MAX_VALUE,mIndex=-1; 
	        
	        //calculating minimum path 
	        
	        int x=0;
	        while(x < rows)
	        {
	        	if(optimalNode[x] > 0)
	        	{
	             if (optimalNode[x] < m)
	             {
		             m = optimalNode[x];
		             mIndex = x;
	             }
	        	}
	        	x++;
	        }
	        if(m==Integer.MAX_VALUE  )
	        {	
	        	System.out.println("Router are Disconnected");
	        	System.out.println("There is no Best Router in the Topology");
	        	System.out.println("Please Use different Network topology using Input command 1");
	        	return;
	        }
	        else if(m <= 0)
	        {
	        	System.out.println("");
	        	System.out.println("There is no Best Router in the Topology");
	        	System.out.println("Please Use different Network topology using Input command 1");
	        	return;
	        }
	        System.out.println("Router "+(mIndex+1)+" is best for broadcast");
	        System.out.println("Destination          Distance from");
	        System.out.println("Router                 Router "+(mIndex+1));
	        System.out.println("==============================================");
	        
	        int t=0;
	        while(t<rows)
	        {
	        	if(shortestdist[mIndex][t]==Integer.MAX_VALUE)
				 {
					 System.out.println(" "+(t+1)+" \t\t\t  0" );
				 }
	        	else
	        	{
	        		System.out.println(" "+(t+1)+" \t\t\t  "+shortestdist[mIndex][t]);
	        	}
	        	t++;
	        }
	        if(srouter!=0)
	        {
	        	ForwardTable(inputMatrix,srouter-1);
	        }
		}

		//Print source to destination
		public static void printPath(int[] upper, int ID)
	    {
	        if (upper[ID]==-1)
	            return;
	     
	        printPath(upper, upper[ID]);
	        System.out.print("->"+(ID+1));
	    }

	
	
	public static void main(String args[]) throws FileNotFoundException
	{
	try
	{
		
     Scanner input;
     input =new Scanner(System.in); 
        while(usercommand != 6)
        {
        	/* List of commands which the user can input */
        	System.out.println("Link State Routing Simulator");
        	System.out.println("==============================================");
            System.out.println("1) Create a Network Topology");
            System.out.println("2) Build a Forward Table");
            System.out.println("3) Shortest Path to Destination Router");
            System.out.println("4) Modify a Topology");
            System.out.println("5) Best Router for Broadcast");
            System.out.println("6) Exit");
            System.out.println("==============================================");
            System.out.println();
        	System.out.println("Enter Input command:"); 
        	
        	int usercommand;
        	usercommand=new Scanner(System.in).nextInt();
        	switch(usercommand)
        	{
        	    /*topology file input from the user*/
        		case 1: 
	        		System.out.println("Input Network  File:");
	        		top_file=input.next();
	        	if(top_file==null)
	        	{
	        		System.out.println("Please Input Network File!");
	        		System.out.println("Network Topology file needed for Link State Routing");
	        		break;
	        	}
	        		Matrix(top_file);
	        		break;
	        		
	        	/*selecting the source router from the network topology*/	
        		case 2:
        			if(top_file == null)
        			{
        				System.out.println("Network Topology file needed for Link State Routing");
        				System.out.println("Please Input Network File!");
        				System.out.println();
        				break;
        			}
        			System.out.println("Please Enter Source Router :");
                    srouter = input.nextInt();//source router input from user
                    if(srouter <1 )
                    {
                    	System.out.println("Enter a valid Source Router!");
                    	System.out.println();
                    }
                    else if(srouter> rows)
                    {
                    	System.out.println("Enter a valid Source Router!");
                    	System.out.println();
                    }
                    else if(state_del_router[srouter-1]==1)    // router removed before
                    	{
                    		System.out.println("The Router is removed from the Network!");
                    		System.out.println("Please Enter a valid router using Input command 2");
                    		System.out.println();
                    		srouter=0;
                    		break;
                    	}
                    else
                    {       	
                    	   ForwardTable(matrix_form,srouter-1);			
                           Print_Forward_Table(srouter-1);			
                    }
                    break;
                    
                /*Destination Node from the Network*/    
        		case 3:
        			if(top_file == null)
        			{
        				System.out.println("Network Topology file needed for Link State Routing");
        				System.out.println("Please Input Network File!");
        				break;
        			}
        			if(srouter <= 0)
        			{
        				System.out.println("You have not selected the Source Router.");
        				System.out.println("Please Enter a Source Router:");
                        srouter = input.nextInt();//source router number goes here
                        if(srouter>0)
                        {
                        	if(srouter<= rows)
                        	{
                        		ForwardTable(matrix_form,srouter-1);
                                System.out.println("Source Router is Set.");
                                System.out.println("Select Your Input Command Again:");
                        	}                           
                        }
                        else
                        {
                            System.out.println("Enter a valid Source Router!");
                        }
                        break;
        			}
        			System.out.println("Select a Destination Router:");
                    drouter = input.nextInt();//source router number goes here
                    if(drouter<1)
                    {
                    	System.out.println("Enter a valid Router Number!");
                    }
                    else if(drouter > rows)
                    {
                    	System.out.println("Enter a valid Router Number!");
                    }
                    else if(state_del_router[drouter-1]==1)
                    	{
                    		System.out.println("The Router has already been removed from the Topology!");
                    		System.out.println("Please Enter a valid router using Input command 3");
                    		drouter=0;
                    		break;
                    	}
                    else
                    {
                    	ShortestPath(drouter-1);   
                    }
                    break;  
                /*deleting a specific router from the input topology*/    
        		case 4:
        			if(top_file == null)
        			{
        				System.out.println("Network Topology file needed for Link State Routing");
        				System.out.println("Please Input Network File!");
        				break;
        			}
        			if(srouter <= 0)
        			{
        				System.out.println("You have not selected the Source Router.");
        				System.out.println("Please Enter a Source Router!");
                        srouter = input.nextInt();//source router number goes here
                        if(srouter>0 )
                        {
                        	if(srouter<= rows)
                        	{
                        		ForwardTable(matrix_form,srouter-1);
                                System.out.println("Source Router is Set.");
                                System.out.println("Select Your Input Command Again:");
                                System.out.println();
                        	}      
                        }
                        else
                        {
                            System.out.println("Please Enter a valid Source Router!");
                            System.out.println();
                        }
                        break;
        			}
        			if(drouter <= 0)
        			{
        				System.out.println("You have not selected the Destination Router.");
        				System.out.println("Please Enter a Destination Router!");
        				System.out.println();
                        drouter = input.nextInt();				//source router number 
                        if(drouter>0)
                        {
                        	if(drouter<= rows)
                        	{
                        		ShortestPath(drouter-1);
                                System.out.println("Destination Router is Set.");
                                System.out.println("Select Your Input Command Again:");
                                System.out.println();
                        	}  
                        }
                        else
                        {
                            System.out.println("Please Enter a valid Destination Router!");
                            System.out.println();
                        }
                        break;
        			}
                    System.out.println("Select a Router to be removed:");
                    del_router = input.nextInt();//delete router number goes here
                    if(del_router>0)
                    {
                    	if(del_router<=rows)
                    	{
                    		deleteRouter(del_router-1);
                    	} 
                    }
                    else
                    {
                        System.out.println("Please Enter a valid Router Number!");
                    }
                    break;  
                //finds the best router    
                case 5:
                	if(top_file == null)
        			{
                		System.out.println("Network Topology file needed for Link State Routing");
        				System.out.println("Please Input Network File!");
        				break;
        			}
                    BestBrodcastRouter(matrix_form);
                    break;
                /*exit Link State Routing Program*/    
                case 6:
                	System.out.println("Exit from Link State Routing Protocol Project.");
					System.out.println("Thank you!!!! Good Byee !!!!");
					System.exit(0);
                default:
                    System.out.println("Please Enter a valid Input Command!");    
        			
        	}
        
        }
	}
	catch(java.util.InputMismatchException err)
	{
		System.out.println("Please Enter a Valid option from the menu");
		System.out.println();
		main(args);
	}
   }	
}

	